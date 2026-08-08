-- Mesmo drift do V007, agora em orders.user_id: a FK viva no banco não tinha
-- ON DELETE CASCADE, mesmo o V001 declarando isso. Sem cascade, DELETE
-- /api/users/{id} falhava com 409 para qualquer usuário que já tivesse feito
-- um pedido. Localiza a constraint pelo nome real (varia por ambiente) em vez
-- de assumir um nome fixo.
DO $$
DECLARE
    fk_name text;
BEGIN
    SELECT tc.constraint_name INTO fk_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
       AND tc.table_schema = kcu.table_schema
    WHERE tc.table_name = 'orders'
      AND tc.constraint_type = 'FOREIGN KEY'
      AND kcu.column_name = 'user_id'
    LIMIT 1;

    IF fk_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE orders DROP CONSTRAINT %I', fk_name);
    END IF;
END $$;

ALTER TABLE orders
    ADD CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
