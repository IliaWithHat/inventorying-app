CREATE TABLE item
(
    id                   BIGSERIAL PRIMARY KEY,
    serial_number        BIGINT      NOT NULL,
    inventory_number     BIGINT      NOT NULL,
    name                 VARCHAR(64) NOT NULL,
    stored_in            VARCHAR(64) NOT NULL,
    quantity             INTEGER     NOT NULL,
    is_owned_by_employee BOOLEAN     NOT NULL,
    additional_info      JSONB,
    image                VARCHAR(128),
    created_at           TIMESTAMP,
    created_by           INTEGER REFERENCES users
);

CREATE TABLE users
(
    id          SERIAL PRIMARY KEY,
    email       VARCHAR(64) NOT NULL,
    first_name  VARCHAR(64) NOT NULL,
    middle_name VARCHAR(64) NOT NULL,
    last_name   VARCHAR(64) NOT NULL,
    phone       VARCHAR(64) NOT NULL,
    role        VARCHAR(12) NOT NULL,
    admin_id    INTEGER REFERENCES users
);