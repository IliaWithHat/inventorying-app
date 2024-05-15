CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    email      VARCHAR(64)  NOT NULL UNIQUE,
    password   VARCHAR(128) NOT NULL,
    first_name VARCHAR(64)  NOT NULL,
    last_name  VARCHAR(64)  NOT NULL,
    phone      VARCHAR(64)  NOT NULL,
    role       VARCHAR(12)  NOT NULL,
    admin_id   INTEGER REFERENCES users ON DELETE CASCADE
);

CREATE INDEX users_admin_id_index ON users (admin_id);
CREATE UNIQUE INDEX users_email_index ON users (email);
CREATE UNIQUE INDEX users_id_admin_id_index ON users (id, admin_id);

CREATE TABLE item_filter
(
    id                   SERIAL PRIMARY KEY,
    stored_in            VARCHAR(64),
    is_owned_by_employee VARCHAR(12),
    user_id              INTEGER NOT NULL REFERENCES users ON DELETE CASCADE
);

CREATE INDEX item_filter_user_id_index ON item_filter (user_id);

CREATE TABLE item_sequence
(
    id         SERIAL PRIMARY KEY,
    last_value BIGINT  NOT NULL,
    user_id    INTEGER NOT NULL REFERENCES users ON DELETE CASCADE
);

CREATE UNIQUE INDEX item_sequence_user_id_index ON item_sequence (user_id);

CREATE TABLE inventory
(
    id               BIGSERIAL PRIMARY KEY,
    inventory_number VARCHAR(24)    NOT NULL,
    current_quantity NUMERIC(10, 3) NOT NULL,
    user_id          INTEGER        NOT NULL REFERENCES users ON DELETE CASCADE
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
    price_per_unit       NUMERIC(10, 2) NOT NULL,
    is_owned_by_employee BOOLEAN        NOT NULL,
    created_at           TIMESTAMP      NOT NULL,
    user_id              INTEGER        NOT NULL REFERENCES users ON DELETE CASCADE
);

CREATE INDEX item_user_id_index ON item (user_id);
CREATE UNIQUE INDEX item_id_user_id_index ON item (id, user_id);
CREATE UNIQUE INDEX item_inventory_number_user_id_index ON item (inventory_number, user_id);