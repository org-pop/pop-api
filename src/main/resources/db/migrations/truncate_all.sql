-- Trunca todas as tabelas de dados do schema, reiniciando os IDs (RESTART IDENTITY)
-- e resolvendo a ordem de FKs automaticamente (CASCADE).
-- NÃO afeta a tabela de controle do Flyway (flyway_schema_history).

TRUNCATE TABLE
    payment,
    order_item,
    orders,
    cart_item,
    cart,
    user_accessibility_profiles,
    user_accessibility_settings,
    phone,
    address,
    product,
    users
RESTART IDENTITY CASCADE;
