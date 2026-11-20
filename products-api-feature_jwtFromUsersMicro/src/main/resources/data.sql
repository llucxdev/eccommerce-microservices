INSERT INTO product_types (name)
VALUES ('Fruit'),
       ('Berry');

INSERT INTO products (name, price, stock, product_type_id)
VALUES ('Apple', 1, 5, 1),
       ('Kiwi', 2, 3, 1),
       ('Strawberry', 3, 8, 2),
       ('Raspberry', 4, 16, 2),
       ('Banana', 2, 4, 1);
