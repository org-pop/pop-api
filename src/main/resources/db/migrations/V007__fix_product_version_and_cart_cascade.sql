-- Corrige o drift entre os arquivos de migration e o schema real do banco
-- compartilhado de dev: o flyway_schema_history já tinha uma entrada órfã de
-- versão 006 (de um experimento anterior que nunca foi commitado — ver
-- FlywayConfig), então o Flyway achava que o schema já estava "up to date" e
-- nunca chegou a rodar o DDL do V005 de fato. Por isso a versão deste arquivo
-- precisa ser maior que a versão órfã (006) pra garantir que o migrate() rode.

-- product.version: sem essa coluna, o @Version da entidade Product quebra com
-- 500 (Hibernate: "column p1_0.version does not exist") em qualquer leitura.
ALTER TABLE product
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

-- cart.user_id -> users.id: a FK viva no banco não tinha ON DELETE CASCADE
-- (mesmo o V001 declarando isso — provavelmente criada antes desse texto
-- existir no arquivo). Sem cascade, DELETE /api/users/{id} falhava com 409
-- sempre que o usuário já tinha um carrinho. Localiza a constraint pelo nome
-- real (que varia por ambiente) em vez de assumir um nome fixo.
DO $$
DECLARE
    fk_name text;
BEGIN
    SELECT tc.constraint_name INTO fk_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
       AND tc.table_schema = kcu.table_schema
    WHERE tc.table_name = 'cart'
      AND tc.constraint_type = 'FOREIGN KEY'
      AND kcu.column_name = 'user_id'
    LIMIT 1;

    IF fk_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE cart DROP CONSTRAINT %I', fk_name);
    END IF;
END $$;

ALTER TABLE cart
    ADD CONSTRAINT cart_user_id_fkey FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
