CREATE TABLE IF NOT EXISTS mooc_roles (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          role_name VARCHAR(50) NOT NULL,
                                          PRIMARY KEY (id),
                                          CONSTRAINT uk_mooc_roles_role_name UNIQUE (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mooc_users (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          account_non_expired BIT NOT NULL,
                                          account_non_locked BIT NOT NULL,
                                          credentials_non_expired BIT NOT NULL,
                                          email VARCHAR(254) NOT NULL,
                                          enabled BIT NOT NULL,
                                          mobile VARCHAR(11) NOT NULL,
                                          name VARCHAR(50) NOT NULL,
                                          password_hash VARCHAR(80) NOT NULL,
                                          username VARCHAR(50) NOT NULL,
                                          PRIMARY KEY (id),
                                          CONSTRAINT uk_mooc_users_username UNIQUE (username),
                                          CONSTRAINT uk_mooc_users_mobile UNIQUE (mobile),
                                          CONSTRAINT uk_mooc_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mooc_users_roles (
                                                user_id BIGINT NOT NULL,
                                                role_id BIGINT NOT NULL,
                                                PRIMARY KEY (user_id, role_id),
                                                CONSTRAINT fk_users_roles_user_id_mooc_users_id FOREIGN KEY (user_id) REFERENCES mooc_users (id),
                                                CONSTRAINT fk_users_roles_role_id_mooc_roles_id FOREIGN KEY (role_id) REFERENCES mooc_roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into mooc_users(id, username, `name`, mobile, password_hash, enabled, account_non_expired, account_non_locked, credentials_non_expired, email)
values (1, 'user', 'Zhang San', '13000000001', '{bcrypt}$2a$10$jhS817qUHgOR4uQSoEBRxO58.rZ1dBCmCTjG8PeuQAX4eISf.zowm', 1, 1, 1, 1, 'zhangsan@local.dev'),
       (2, 'old_user', 'Li Si', '13000000002', '{SHA-1}7ce0359f12857f2a90c7de465f40a95f01cb5da9', 1, 1, 1, 1, 'lisi@local.dev');
insert into mooc_roles(id, role_name) values (1, 'ROLE_USER'), (2, 'ROLE_ADMIN');
insert into mooc_users_roles(user_id, role_id) values (1, 1), (1, 2), (2, 1);