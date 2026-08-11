-- Mesmo drift documentado em V004/V007/V008/V009: o V001 já declarava
-- UNIQUE (cart_id, product_id) em cart_item, mas essa constraint não está
-- de fato aplicada no banco compartilhado de dev. Sem ela, dois "adicionar
-- ao carrinho" concorrentes para o mesmo produto (ver lock adicionado em
-- CartService.addItemToCart) inseriam duas linhas em vez de uma, somando
-- uma quantidade maior que o estoque disponível.

-- Antes de recriar a constraint, funde linhas duplicadas já existentes:
-- mantém a linha de menor id, soma as quantidades das demais (limitada ao
-- estoque do produto) e remove o excesso.
DO $$
DECLARE
    dup RECORD;
    keep_id BIGINT;
    total_qty INTEGER;
    capped_qty INTEGER;
    product_stock INTEGER;
BEGIN
    FOR dup IN
        SELECT cart_id, product_id
        FROM cart_item
        GROUP BY cart_id, product_id
        HAVING COUNT(*) > 1
    LOOP
        SELECT MIN(id) INTO keep_id
        FROM cart_item
        WHERE cart_id = dup.cart_id AND product_id = dup.product_id;

        SELECT SUM(quantity) INTO total_qty
        FROM cart_item
        WHERE cart_id = dup.cart_id AND product_id = dup.product_id;

        SELECT stock INTO product_stock
        FROM product
        WHERE id = dup.product_id;

        -- cart_item.quantity tem CHECK (quantity > 0): se o produto está sem
        -- estoque, não há linha válida para manter, então remove o grupo inteiro.
        IF product_stock IS NULL OR product_stock <= 0 THEN
            DELETE FROM cart_item
            WHERE cart_id = dup.cart_id AND product_id = dup.product_id;
            CONTINUE;
        END IF;

        capped_qty := LEAST(total_qty, product_stock);

        DELETE FROM cart_item
        WHERE cart_id = dup.cart_id
          AND product_id = dup.product_id
          AND id <> keep_id;

        UPDATE cart_item
        SET quantity = capped_qty
        WHERE id = keep_id;
    END LOOP;
END $$;

-- Reaplica a constraint só se ela não existir de verdade no banco atual.
DO $$
DECLARE
    constraint_exists BOOLEAN;
BEGIN
    -- Considera existente só uma UNIQUE cujas colunas sejam exatamente
    -- (cart_id, product_id) — não basta ter alguma UNIQUE na tabela.
    SELECT EXISTS (
        SELECT tc.constraint_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
            ON tc.constraint_name = kcu.constraint_name
           AND tc.table_schema = kcu.table_schema
        WHERE tc.table_name = 'cart_item'
          AND tc.constraint_type = 'UNIQUE'
        GROUP BY tc.constraint_name
        HAVING ARRAY_AGG(kcu.column_name::text ORDER BY kcu.column_name) = ARRAY['cart_id', 'product_id']::text[]
    ) INTO constraint_exists;

    IF NOT constraint_exists THEN
        ALTER TABLE cart_item
            ADD CONSTRAINT uk_cart_item_cart_product UNIQUE (cart_id, product_id);
    END IF;
END $$;
