insert into users (id, first_name, last_name, password, username) values (1, 'firstName1', 'lastname1', '$2a$10$5dTPU5sKvZwoaELnLy.MHuMLwmsXID3efg0yJoU.7VKGMGAgCQ1hm', 'username1');
insert into users (id, first_name, last_name, password, username) values (2, 'firstName2', 'lastname2', '$2a$10$ZYuE6BBlRqSNCoxs68/BhuuWjFpAd/jQuPnkmS/xk6wpzbS3PW9uO', 'username2');
insert into users (id, first_name, last_name, password, username) values (3, 'firstName3', 'lastname3', '$2a$10$owZ/to7HEqWX28FZzyuYB.04soDSNNHYwGBhbnYwOndKtzT5KOYg.', 'username3');
insert into users (id, first_name, last_name, password, username) values (4, 'firstName4', 'lastname4', '$2a$10$XtyMJbxaHJsdo7XGY7Wt0uPZf1mNn15WHvaVWmOH5TGIpzF2ncTmS', 'username4');
insert into users (id, first_name, last_name, password, username) values (5, 'firstName5', 'lastname5', 'password5', 'username5');

insert into groups (id, name, admin_id) values (1, 'group1', 1);
insert into groups (id, name, admin_id) values (2, 'group2', 2);

insert into user_group (group_id, user_id) values (1, 3);
insert into user_group (group_id, user_id) values (2, 1);
insert into user_group (group_id, user_id) values (2, 4);