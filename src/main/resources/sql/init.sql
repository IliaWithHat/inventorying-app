CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    email      VARCHAR(64)  NOT NULL UNIQUE,
    password   VARCHAR(128) NOT NULL,
    first_name VARCHAR(64)  NOT NULL,
    last_name  VARCHAR(64)  NOT NULL,
    phone      VARCHAR(64)  NOT NULL,
    role       VARCHAR(12)  NOT NULL,
    admin_id   INTEGER REFERENCES users
);

CREATE TABLE inventory
(
    id               BIGSERIAL PRIMARY KEY,
    inventory_number VARCHAR(24)    NOT NULL,
    current_quantity NUMERIC(10, 3) NOT NULL,
    user_id          INTEGER REFERENCES users
);

CREATE TABLE item
(
    id                   BIGSERIAL PRIMARY KEY,
    serial_number        BIGINT         NOT NULL,
    name                 VARCHAR(64)    NOT NULL,
    inventory_number     VARCHAR(24)    NOT NULL,
    stored_in            VARCHAR(64)    NOT NULL,
    unit                 VARCHAR(12)    NOT NULL,
    quantity             NUMERIC(10, 3) NOT NULL,
    price_per_unit       NUMERIC(12, 2) NOT NULL,
    is_owned_by_employee BOOLEAN        NOT NULL,
    created_at           TIMESTAMP      NOT NULL,
    created_by           INTEGER        NOT NULL REFERENCES users
);
