--liquibase formatted sql

--changeset tuyen:001-initial-schemas
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE products (
    sku VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(19,2) NOT NULL CHECK (price >= 0),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE promotions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) NOT NULL,
    value NUMERIC(19,2) NOT NULL CHECK (value >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_promotion_active ON promotions(active);

CREATE TABLE coupons (
     code VARCHAR(50) PRIMARY KEY,
     discount_amount NUMERIC(19,2) NOT NULL CHECK (discount_amount >= 0),
     active BOOLEAN NOT NULL DEFAULT TRUE,
     expiry_date TIMESTAMP NOT NULL,
     usage_limit INT NOT NULL,
     used_count INT NOT NULL DEFAULT 0,
     version BIGINT NOT NULL DEFAULT 0,
     created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_coupons_expiry_date ON coupons(expiry_date);

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_type VARCHAR(50) NOT NULL,
    subtotal NUMERIC(19,2) NOT NULL CHECK (subtotal >= 0),
    total_discount NUMERIC(19,2) NOT NULL DEFAULT 0 CHECK (total_discount >= 0),
    final_price NUMERIC(19,2) NOT NULL CHECK (final_price >= 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_orders_customer_type_created_at ON orders(customer_type, created_at);

CREATE TABLE order_items (
     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     order_id UUID NOT NULL,
     sku VARCHAR(50) NOT NULL,
     price NUMERIC(19,2) NOT NULL CHECK (price >= 0),
     quantity INTEGER NOT NULL CHECK (quantity > 0),

     CONSTRAINT fk_order_items_order
         FOREIGN KEY (order_id)
            REFERENCES orders(id)
            ON DELETE CASCADE,

     CONSTRAINT fk_order_items_product
         FOREIGN KEY (sku)
             REFERENCES products(sku)
             ON DELETE CASCADE,

     CONSTRAINT uk_order_items_order_sku
         UNIQUE (order_id, sku)
);

CREATE INDEX idx_order_items_sku ON order_items(sku);