-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 清除现有数据并重置自增ID
TRUNCATE TABLE sys_user_role;
TRUNCATE TABLE sys_role_permission;
TRUNCATE TABLE sys_user;
TRUNCATE TABLE sys_role;
TRUNCATE TABLE sys_permission;

-- 创建权限

INSERT INTO sys_permission (name, code, type, enabled, create_time, update_time) VALUES
('公司查询', 'system:company:list', 1, 0, NOW(), NOW()),
('公司新增', 'system:company:add', 1, 0, NOW(), NOW()),
('公司编辑', 'system:company:edit', 1, 0, NOW(), NOW()),
('公司删除', 'system:company:delete', 1, 0, NOW(), NOW()),
('部门查询', 'system:department:list', 1, 0, NOW(), NOW()),
('部门新增', 'system:department:add', 1, 0, NOW(), NOW()),
('部门编辑', 'system:department:edit', 1, 0, NOW(), NOW()),
('部门删除', 'system:department:delete', 1, 0, NOW(), NOW()),
('用户查询', 'system:user:list', 1, 0, NOW(), NOW()),
('用户新增', 'system:user:add', 1, 0, NOW(), NOW()),
('用户编辑', 'system:user:edit', 1, 0, NOW(), NOW()),
('用户删除', 'system:user:delete', 1, 0, NOW(), NOW()),
('用户导出', 'system:user:export', 1, 0, NOW(), NOW()),
('重置密码', 'system:user:resetPwd', 1, 0, NOW(), NOW()),
('角色查询', 'system:role:list', 1, 0, NOW(), NOW()),
('角色新增', 'system:role:add', 1, 0, NOW(), NOW()),
('角色编辑', 'system:role:edit', 1, 0, NOW(), NOW()),
('角色删除', 'system:role:delete', 1, 0, NOW(), NOW()),
('菜单查询', 'system:menu:list', 1, 0, NOW(), NOW()),
('菜单新增', 'system:menu:add', 1, 0, NOW(), NOW()),
('菜单编辑', 'system:menu:edit', 1, 0, NOW(), NOW()),
('菜单删除', 'system:menu:delete', 1, 0, NOW(), NOW()),
('权限查询', 'system:permission:list', 1, 0, NOW(), NOW()),
('权限新增', 'system:permission:add', 1, 0, NOW(), NOW()),
('权限编辑', 'system:permission:edit', 1, 0, NOW(), NOW()),
('权限删除', 'system:permission:delete', 1, 0, NOW(), NOW())
;

-- 创建角色
INSERT INTO sys_role (id, name, code, enabled, data_scope, create_time, update_time) VALUES
(1, '超级管理员', 'admin', 0, 1, NOW(), NOW()),
(2, '普通用户', 'user', 0, 1, NOW(), NOW()),
(3, '访客', 'visitor', 0, 1, NOW(), NOW());

-- 分配角色权限
-- 超级管理员拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 1, id FROM sys_permission;

-- 普通用户拥有查询权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE code LIKE '%:query';

-- 访客只有基本查询权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 3, id FROM sys_permission WHERE code IN ('system:user:query', 'system:role:query');

-- 创建用户
INSERT INTO sys_user (id, username, password, nickname, email, phone, gender, avatar, enabled, create_time, update_time) VALUES
-- 使用 BCrypt 加密的密码，所有用户的初始密码都是 '123456'
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 'admin@example.com', '13800000001', 1, '/avatar/default.jpg', 0, NOW(), NOW()),
(2, 'test', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '测试用户', 'test@example.com', '13800000002', 1, '/avatar/default.jpg', 0, NOW(), NOW()),
(3, 'guest', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '访客用户', 'guest@example.com', '13800000003', 0, '/avatar/default.jpg', 0, NOW(), NOW());

-- 分配用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), -- admin -> 超级管理员
(2, 2), -- test -> 普通用户
(3, 3); -- guest -> 访客

SET FOREIGN_KEY_CHECKS = 1;