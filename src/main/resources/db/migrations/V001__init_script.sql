-- extensão uuid
CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

-- tabela users
CREATE TABLE users
(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(255)        NOT NULL,
    email           VARCHAR(255) UNIQUE NOT NULL,
    password        VARCHAR(255)        NOT NULL,
    created_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    account_balance DECIMAL(10, 2)   DEFAULT 0.0
);

-- tabela address
CREATE TABLE address
(
    id       BIGSERIAL PRIMARY KEY,
    user_id  UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    street   VARCHAR(255) NOT NULL,
    number   VARCHAR(50)  NOT NULL,
    city     VARCHAR(100) NOT NULL,
    state    CHAR(2)      NOT NULL,
    zip_code VARCHAR(10)  NOT NULL
);

-- tabela phone
CREATE TABLE phone
(
    id      BIGSERIAL PRIMARY KEY,
    user_id UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    number  VARCHAR(20) NOT NULL
);

-- tabela product
CREATE TABLE product
(
    id                     BIGSERIAL PRIMARY KEY,
    name                   VARCHAR(255)   NOT NULL,
    description            TEXT,
    price                  DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock                  INTEGER        NOT NULL DEFAULT 0 CHECK (stock >= 0),
    image_url              VARCHAR(500),
    image_alt_text         VARCHAR(255),
    accessible_description TEXT,
    color_palette          VARCHAR(100),
    high_contrast          BOOLEAN                 DEFAULT FALSE,
    franchise              VARCHAR(100),
    rarity                 VARCHAR(50)
);

-- tabela cart
CREATE TABLE cart
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id)
);

-- tabela cart_item
CREATE TABLE cart_item
(
    id         BIGSERIAL PRIMARY KEY,
    cart_id    BIGINT  NOT NULL REFERENCES cart (id) ON DELETE CASCADE,
    product_id BIGINT  NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    quantity   INTEGER NOT NULL CHECK (quantity > 0),
    UNIQUE (cart_id, product_id)
);

-- tabela orders
CREATE TABLE orders
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    UUID           NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    total      DECIMAL(10, 2) NOT NULL CHECK (total >= 0),
    status     VARCHAR(50)    NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);

-- tabela order_item
CREATE TABLE order_item
(
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id BIGINT         NOT NULL REFERENCES product (id) ON DELETE RESTRICT,
    quantity   INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0)
);

-- tabela payment
CREATE TABLE payment
(
    id       BIGSERIAL PRIMARY KEY,
    order_id BIGINT      NOT NULL REFERENCES orders (id) ON DELETE RESTRICT,
    method   VARCHAR(50) NOT NULL,
    status   VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);

-- índices
CREATE INDEX idx_address_user_id ON address (user_id);
CREATE INDEX idx_phone_user_id ON phone (user_id);
CREATE INDEX idx_cart_user_id ON cart (user_id);
CREATE INDEX idx_cart_item_cart_id ON cart_item (cart_id);
CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_order_item_order_id ON order_item (order_id);
CREATE INDEX idx_payment_order_id ON payment (order_id);
CREATE INDEX idx_product_franchise ON product (franchise);
CREATE INDEX idx_product_price ON product (price);