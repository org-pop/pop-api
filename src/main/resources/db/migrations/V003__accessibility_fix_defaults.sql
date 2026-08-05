-- Alinha os defaults do schema com os valores aceitos pela API (minúsculos),
-- corrigindo inconsistência entre V002 e as anotações @Pattern dos DTOs.

ALTER TABLE user_accessibility_settings
    ALTER COLUMN font_size_preference SET DEFAULT 'normal';

ALTER TABLE user_accessibility_settings
    ALTER COLUMN color_theme SET DEFAULT 'default';

UPDATE user_accessibility_settings
SET font_size_preference = 'normal'
WHERE font_size_preference = 'NORMAL';

UPDATE user_accessibility_settings
SET color_theme = 'default'
WHERE color_theme = 'DEFAULT';
