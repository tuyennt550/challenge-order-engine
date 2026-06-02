--liquibase formatted sql

--changeset tuyen:002-seed-data
INSERT INTO products (sku, name, price)
VALUES
    ('A100', 'Product A', 100),
    ('B200', 'Product B', 50),
    ('C300', 'Product C', 200);

INSERT INTO promotions (id, type, value, active, created_at)
VALUES
    (gen_random_uuid(), 'PERCENTAGE_DISCOUNT',10,TRUE,CURRENT_TIMESTAMP),
    (gen_random_uuid(),'BUY_2_GET_1_FREE',2,TRUE,CURRENT_TIMESTAMP),
    (gen_random_uuid(),'VIP_DISCOUNT',5,TRUE,CURRENT_TIMESTAMP);

INSERT INTO coupons (code, discount_amount, active, expiry_date, usage_limit)
VALUES
    ('SUMMER10',10,TRUE,CURRENT_TIMESTAMP + INTERVAL '30 days', 10),
    ('SAVE20',20,TRUE,CURRENT_TIMESTAMP + INTERVAL '30 days', 10);