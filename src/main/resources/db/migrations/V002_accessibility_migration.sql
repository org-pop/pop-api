-- Configurações de acessibilidade por usuário
CREATE TABLE user_accessibility_settings
(
    id                   BIGSERIAL PRIMARY KEY,
    user_id              UUID        NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    preferred_language   VARCHAR(10) NOT NULL DEFAULT 'pt-BR',
    simplified_language  BOOLEAN     NOT NULL DEFAULT FALSE,
    screen_reader_mode   BOOLEAN     NOT NULL DEFAULT FALSE,
    font_size_preference VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    color_theme          VARCHAR(20) NOT NULL DEFAULT 'DEFAULT'
);

-- Perfis de acessibilidade (um usuário pode ter vários)
CREATE TABLE user_accessibility_profiles
(
    settings_id BIGINT      NOT NULL REFERENCES user_accessibility_settings (id) ON DELETE CASCADE,
    profile     VARCHAR(50) NOT NULL,
    PRIMARY KEY (settings_id, profile)
);

CREATE INDEX idx_accessibility_user_id ON user_accessibility_settings (user_id);