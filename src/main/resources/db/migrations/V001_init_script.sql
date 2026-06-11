-- Drop all tables
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS cart_item CASCADE;
DROP TABLE IF EXISTS cart CASCADE;
DROP TABLE IF EXISTS phone CASCADE;
DROP TABLE IF EXISTS address CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop all sequences
DROP SEQUENCE IF EXISTS address_id_seq CASCADE;
DROP SEQUENCE IF EXISTS phone_id_seq CASCADE;
DROP SEQUENCE IF EXISTS product_id_seq CASCADE;
DROP SEQUENCE IF EXISTS cart_id_seq CASCADE;
DROP SEQUENCE IF EXISTS cart_item_id_seq CASCADE;
DROP SEQUENCE IF EXISTS orders_id_seq CASCADE;
DROP SEQUENCE IF EXISTS order_item_id_seq CASCADE;
DROP SEQUENCE IF EXISTS payment_id_seq CASCADE;

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
    account_balance DECIMAL(10, 2)   DEFAULT 0.0,
    updated_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

-- tabela address
CREATE TABLE address
(
    id         SERIAL PRIMARY KEY,
    user_id    UUID         NOT NULL,
    street     VARCHAR(255) NOT NULL,
    number     VARCHAR(50)  NOT NULL,
    complement VARCHAR(255),
    city       VARCHAR(100) NOT NULL,
    state      CHAR(2)      NOT NULL,
    zip_code   VARCHAR(10)  NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- tabela phone
CREATE TABLE phone
(
    id         SERIAL PRIMARY KEY,
    user_id    UUID        NOT NULL,
    number     VARCHAR(20) NOT NULL,
    type       VARCHAR(20) DEFAULT 'celular',
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- tabela product
CREATE TABLE product
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock       INTEGER        NOT NULL DEFAULT 0 CHECK (stock >= 0),
    image_url   VARCHAR(500),
    franchise   VARCHAR(100),
    rarity      VARCHAR(50),
    active      BOOLEAN                 DEFAULT TRUE,
    created_at  TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);

-- tabela cart
CREATE TABLE cart
(
    id         SERIAL PRIMARY KEY,
    user_id    UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (user_id)
);

-- tabela cart_item
CREATE TABLE cart_item
(
    id         SERIAL PRIMARY KEY,
    cart_id    INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity   INTEGER NOT NULL CHECK (quantity > 0),
    added_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES cart (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE,
    UNIQUE (cart_id, product_id)
);

-- tabela orders
CREATE TABLE orders
(
    id         SERIAL PRIMARY KEY,
    user_id    UUID           NOT NULL,
    total      DECIMAL(10, 2) NOT NULL CHECK (total >= 0),
    status     VARCHAR(50) DEFAULT 'pending',
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    address_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (address_id) REFERENCES address (id) ON DELETE SET NULL
);

-- tabela order_item
CREATE TABLE order_item
(
    id          SERIAL PRIMARY KEY,
    order_id    INTEGER        NOT NULL,
    product_id  INTEGER        NOT NULL,
    quantity    INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price  DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(10, 2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT
);

-- tabela payment
CREATE TABLE payment
(
    id         SERIAL PRIMARY KEY,
    order_id   INTEGER        NOT NULL,
    method     VARCHAR(50)    NOT NULL,
    status     VARCHAR(50) DEFAULT 'pending',
    amount     DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    paid_at    TIMESTAMP,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE RESTRICT
);

-- índices table address
CREATE INDEX idx_address_user_id ON address (user_id);

-- índices table phone
CREATE INDEX idx_phone_user_id ON phone (user_id);

-- índices table cart
CREATE INDEX idx_cart_user_id ON cart (user_id);

-- índices table cart_item
CREATE INDEX idx_cart_item_cart_id ON cart_item (cart_id);
CREATE INDEX idx_cart_item_product_id ON cart_item (product_id);

-- índices table orders
CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_created_at ON orders (created_at);

-- índices table order_item
CREATE INDEX idx_order_item_order_id ON order_item (order_id);
CREATE INDEX idx_order_item_product_id ON order_item (product_id);

-- índices table payment
CREATE INDEX idx_payment_order_id ON payment (order_id);
CREATE INDEX idx_payment_status ON payment (status);

-- índices table product
CREATE INDEX idx_product_franchise ON product (franchise);
CREATE INDEX idx_product_rarity ON product (rarity);
CREATE INDEX idx_product_price ON product (price);
CREATE INDEX idx_product_stock ON product (stock) WHERE stock > 0;

-- função para atualizar updated_at
CREATE
OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at
= CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$
language 'plpgsql';

-- triggers para updated_at
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE
    ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_product_updated_at
    BEFORE UPDATE
    ON product
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_cart_updated_at
    BEFORE UPDATE
    ON cart
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_orders_updated_at
    BEFORE UPDATE
    ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();