-- Mesmo drift do V007/V008, agora auditado e corrigido de uma vez para todas as
-- FKs que os migrations originais declaram como ON DELETE CASCADE mas que, no
-- banco compartilhado de dev, não têm cascade de verdade (schema criado fora
-- do fluxo normal do Flyway em algum momento — ver comentário do FlywayConfig).
--
-- Sem isso, excluir um usuário (ou um carrinho/pedido) falha com 409 assim que
-- ele tiver qualquer endereço, telefone, configuração de acessibilidade, item
-- de carrinho ou item de pedido — a exclusão só "funciona" por acidente para
-- contas de teste vazias.
DO $$
DECLARE
    fk_name text;
    tbl text;
    col text;
    ref_tbl text;
    fk_pairs text[][] := ARRAY[
        ARRAY['address', 'user_id', 'users'],
        ARRAY['phone', 'user_id', 'users'],
        ARRAY['user_accessibility_settings', 'user_id', 'users'],
        ARRAY['user_accessibility_profiles', 'settings_id', 'user_accessibility_settings'],
        ARRAY['cart_item', 'cart_id', 'cart'],
        ARRAY['cart_item', 'product_id', 'product'],
        ARRAY['order_item', 'order_id', 'orders']
    ];
    pair text[];
BEGIN
    FOREACH pair SLICE 1 IN ARRAY fk_pairs
    LOOP
        tbl := pair[1];
        col := pair[2];
        ref_tbl := pair[3];

        SELECT tc.constraint_name INTO fk_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
            ON tc.constraint_name = kcu.constraint_name
           AND tc.table_schema = kcu.table_schema
        WHERE tc.table_name = tbl
          AND tc.constraint_type = 'FOREIGN KEY'
          AND kcu.column_name = col
        LIMIT 1;

        IF fk_name IS NOT NULL THEN
            EXECUTE format('ALTER TABLE %I DROP CONSTRAINT %I', tbl, fk_name);
        END IF;

        EXECUTE format(
            'ALTER TABLE %I ADD CONSTRAINT %I FOREIGN KEY (%I) REFERENCES %I (id) ON DELETE CASCADE',
            tbl, tbl || '_' || col || '_fkey', col, ref_tbl
        );
    END LOOP;
END $$;
