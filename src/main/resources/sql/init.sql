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



INSERT INTO users(email, password, first_name, last_name, phone, role, admin_id)
VALUES ('test', '{noop}1234', 'Ilia', 'Rozhko', '+380-96-873-77-76', 'ADMIN', null);

INSERT INTO item_sequence(last_value, user_id)
VALUES (21, 1);

INSERT INTO item(serial_number, name, inventory_number, stored_in, unit, quantity, price_per_unit, is_owned_by_employee,
                 created_at, user_id)
VALUES ('1', 'Chair', '00000001', 'Room 1', 'PC', 1, 50, false, '2024-01-19', 1),
       ('2', 'Chair', '00000002', 'Room 1', 'PC', 1, 50, false, '2024-01-19', 1),
       ('3', 'Chair', '00000003', 'Room 1', 'PC', 1, 50, false, '2024-01-19', 1),
       ('4', 'Chair', '00000004', 'Room 1', 'PC', 1, 50, false, '2024-01-19', 1),
       ('5', 'Chair', '00000005', 'Room 1', 'PC', 1, 50, false, '2024-01-19', 1),
       ('6', 'Chair', '00000006', 'Room 1', 'PC', 1, 50, false, '2024-01-19', 1),
       ('7', 'Table', '00000007', 'Room 1', 'PC', 1, 75, false, '2024-01-19', 1),
       ('8', 'Table', '00000008', 'Room 1', 'PC', 1, 75, false, '2024-01-19', 1),
       ('9', 'Closet', '00000009', 'Room 1', 'PC', 1, 120, false, '2024-01-19', 1),
       ('10', 'Wire', '00000010', 'Warehouse', 'M', 5.1, 20, false, '2024-01-19', 1),
       ('11', 'Cable', '00000011', 'Warehouse', 'M', 8.55, 70.12, false, '2024-01-19', 1),
       ('12', 'Net', '00000012', 'Warehouse', 'M2', 0.4, 0.01, false, '2024-01-19', 1),
       ('13', 'Glasses', '00000013', 'Warehouse', 'PC', 1, 15, false, '2024-01-19', 1),
       ('14', 'Engine', '00000014', 'Warehouse', 'PC', 1, 3000, false, '2024-01-19', 1),
       ('15', 'Thermometer', '00000015', 'Warehouse', 'PC', 10, 13, false, '2024-01-19', 1),
       ('16', 'Boots', '00000016', 'Warehouse', 'PAIR', 5, 1230.33, false, '2024-01-19', 1),
       ('17', 'Wallpaper white', '00000017', 'Warehouse', 'ROLL', 34, 32.32, false, '2024-01-19', 1),
       ('18', 'Wallpaper black', '00000018', 'Warehouse', 'ROLL', 22, 92.88, false, '2024-01-19', 1),
       ('19', 'Wallpaper blue', '00000019', 'Warehouse', 'ROLL', 76, 10, false, '2024-01-19', 1),
       ('20', 'Sand', '00000020', 'Warehouse', 'T', 3.322, 939.05, false, '2024-01-19', 1),
       ('21', 'Calendar', '000001-01', 'Room 2', 'PC', 1.000, 0.00, true, '2024-01-19', 1);
