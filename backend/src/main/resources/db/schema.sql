-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    gender INTEGER,
    avatar VARCHAR(200),
    status INTEGER DEFAULT 0,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    version INTEGER DEFAULT 0,
    remark VARCHAR(500),
    deleted BOOLEAN DEFAULT FALSE
);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    status INTEGER DEFAULT 0,
    data_scope INTEGER DEFAULT 1,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    version INTEGER DEFAULT 0,
    remark VARCHAR(500),
    deleted BOOLEAN DEFAULT FALSE
);

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    type INTEGER,
    status INTEGER DEFAULT 0,
    create_time DATETIME NOT NULL,
    update_time DATETIME,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    version INTEGER DEFAULT 0,
    remark VARCHAR(500),
    deleted BOOLEAN DEFAULT FALSE
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id)
);

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    role_id BIGINT,
    permission_id BIGINT,
    PRIMARY KEY (role_id, permission_id)
);