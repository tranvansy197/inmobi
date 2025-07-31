insert into roles (role_id, role_name) values
        (1, 'ROLE_ADMIN'),
        (2, 'ROLE_USER');

insert into users (email, password, score, turns, role_id) values
        ('admin@gmail.com', '$2a$10$exXVWfnIvIk7Jf2KznYSD.8gflCPQXrFgYc.j410YwjGmLxl5goEG', 0, 0, 1);