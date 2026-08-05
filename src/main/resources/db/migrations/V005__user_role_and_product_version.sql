
-- User.role: usado pelo UserService.loadUserByUsername para montar as authorities
-- e pelos @PreAuthorize("hasRole('ADMIN')") dos controllers.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER';

-- Product.version: coluna do @Version. Sem ela o optimistic locking do produto
-- (escritas concorrentes fora do checkout) não existia.
ALTER TABLE product
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
