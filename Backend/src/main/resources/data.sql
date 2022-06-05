SET REFERENTIAL_INTEGRITY FALSE;
INSERT INTO
    recipes(recipe_id, recipe_name, recipe_category, instructions, suggestions, servings, vegetarian, created_at, modified_date, created_by, modified_by)
VALUES(100, 'starter', 0, 'cook a starter dish', 'should be easy to cook', 10, false, '05-06-2022 11:11', now(), 'user', 'user');

INSERT INTO
    recipes(recipe_id, recipe_name, recipe_category, instructions, suggestions, servings, vegetarian, created_at, modified_date, created_by, modified_by)
VALUES(101, 'main course', 1, 'this takes some time to cook', 'make it tasty', 20, false, '05-06-2022 12:12', now(), 'user', 'user');

INSERT INTO
    recipes(recipe_id, recipe_name, recipe_category, instructions, suggestions, servings, vegetarian, created_at, modified_date, created_by, modified_by)
VALUES(103, 'dessert', 2, 'should be sweet', 'add sugar', 5, true, '05-06-2022 13:13', now(), 'user', 'user');

INSERT INTO roles(id, role_name) VALUES (1, 'admin');
INSERT INTO roles(id, role_name)
VALUES (2, 'user');
INSERT INTO roles(id, role_name) VALUES (3, 'ADMIN');
INSERT INTO roles(id, role_name)
VALUES (4, 'USER');

