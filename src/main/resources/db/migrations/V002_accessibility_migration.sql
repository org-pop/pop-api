
-- Tabela principal de configurações de acessibilidade por usuário
CREATE TABLE IF NOT EXISTS user_accessibility_settings (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    preferred_language  VARCHAR(10)  NOT NULL DEFAULT 'pt-BR',
    simplified_language BOOLEAN      NOT NULL DEFAULT FALSE,
    screen_reader_mode  BOOLEAN      NOT NULL DEFAULT FALSE,
    font_size_preference VARCHAR(20) NOT NULL DEFAULT 'normal',
    color_theme         VARCHAR(20)  NOT NULL DEFAULT 'default'
    );

-- Perfis de acessibilidade do usuário (um usuário pode ter vários)
CREATE TABLE IF NOT EXISTS user_accessibility_profiles (
    settings_id BIGINT      NOT NULL REFERENCES user_accessibility_settings(id) ON DELETE CASCADE,
    profile     VARCHAR(50) NOT NULL,
    PRIMARY KEY (settings_id, profile)
    );

-- Campos de acessibilidade na tabela de produtos
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS accessible_description TEXT,
    ADD COLUMN IF NOT EXISTS image_alt_text         VARCHAR(255),
    ADD COLUMN IF NOT EXISTS color_palette          VARCHAR(100),
    ADD COLUMN IF NOT EXISTS high_contrast          BOOLEAN NOT NULL DEFAULT FALSE;

-- Índice para filtrar produtos por alto contraste
CREATE INDEX IF NOT EXISTS idx_products_high_contrast
    ON products(high_contrast);
