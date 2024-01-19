INSERT INTO users(email, password, first_name, last_name, phone, role, admin_id)
VALUES ('admin', '{noop}admin', 'Ilia', 'Rozhko', '123-123-12-12', 'ADMIN', null);

INSERT INTO item(serial_number, name, inventory_number, stored_in, unit, quantity, price, is_owned_by_employee,
                 created_at, created_by)
VALUES ('1', 'Chair', '00000001', 'Room 1', 'Pc', 1, 50, false, '2024-01-19', 1),
       ('2', 'Chair', '00000002', 'Room 1', 'Pc', 1, 50, false, '2024-01-19', 1),
       ('3', 'Chair', '00000003', 'Room 1', 'Pc', 1, 50, false, '2024-01-19', 1),
       ('4', 'Chair', '00000004', 'Room 1', 'Pc', 1, 50, false, '2024-01-19', 1),
       ('5', 'Chair', '00000005', 'Room 1', 'Pc', 1, 50, false, '2024-01-19', 1),
       ('6', 'Chair', '00000006', 'Room 1', 'Pc', 1, 50, false, '2024-01-19', 1),
       ('7', 'Table', '00000007', 'Room 1', 'Pc', 1, 75, false, '2024-01-19', 1),
       ('8', 'Table', '00000008', 'Room 1', 'Pc', 1, 75, false, '2024-01-19', 1),
       ('9', 'Closet', '00000009', 'Room 1', 'Pc', 1, 120, false, '2024-01-19', 1),
       ('10', 'Wire', '00000010', 'Warehouse', 'M', 5.1, 25, false, '2024-01-19', 1),
       ('11', 'Cable', '00000011', 'Warehouse', 'M', 8.5, 70, false, '2024-01-19', 1),
       ('12', 'Net', '00000012', 'Warehouse', 'M2', 0.4, 0.0, false, '2024-01-19', 1),
       ('13', 'Glasses', '00000013', 'Warehouse', 'Pc', 1, 15, false, '2024-01-19', 1),
       ('14', 'Engine', '00000014', 'Warehouse', 'Pc', 1, 2000, false, '2024-01-19', 1),
       ('15', 'Thermometer', '00000015', 'Warehouse', 'Pc', 10, 133, false, '2024-01-19', 1),
       ('16', 'Boots', '00000016', 'Warehouse', 'Pair', 5, 120, false, '2024-01-19', 1),
       ('17', 'Wallpaper white', '00000017', 'Warehouse', 'Roll', 34, 1333, false, '2024-01-19', 1),
       ('18', 'Wallpaper black', '00000018', 'Warehouse', 'Roll', 22, 9288, false, '2024-01-19', 1),
       ('19', 'Wallpaper blue', '00000019', 'Warehouse', 'Roll', 76, 100, false, '2024-01-19', 1),
       ('20', 'Sand', '00000020', 'Warehouse', 'T', 3.32, 9382, false, '2024-01-19', 1);