CREATE TABLE IF NOT EXISTS mooc_permissions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    permission_name VARCHAR(50) NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_mooc_permissions_permission_name UNIQUE (permission_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mooc_roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    built_in BIT NOT NULL,
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
    mfa_key VARCHAR(255) NOT NULL,
    mobile VARCHAR(11) NOT NULL,
    name VARCHAR(50) NOT NULL,
    password_hash VARCHAR(80) NOT NULL,
    username VARCHAR(50) NOT NULL,
    using_mfa BIT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_mooc_users_username UNIQUE (username),
    CONSTRAINT uk_mooc_users_mobile UNIQUE (mobile),
    CONSTRAINT uk_mooc_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mooc_roles_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_roles_permissions_role_id_mooc_roles_id FOREIGN KEY (role_id) REFERENCES mooc_roles (id),
    CONSTRAINT fk_roles_permissions_permission_id_mooc_permissions_id FOREIGN KEY (permission_id) REFERENCES mooc_permissions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mooc_users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_users_roles_user_id_mooc_users_id FOREIGN KEY (user_id) REFERENCES mooc_users (id),
    CONSTRAINT fk_users_roles_role_id_mooc_roles_id FOREIGN KEY (role_id) REFERENCES mooc_roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
