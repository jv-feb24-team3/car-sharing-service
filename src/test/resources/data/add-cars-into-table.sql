DELETE FROM cars;
INSERT INTO cars (id, brand, type, inventory, daily_fee, is_deleted)
VALUES
    (1, 'Audi', 'SEDAN', 10, 100.0, 0),
    (2, 'Tesla', 'SEDAN', 11, 180.0, 0);