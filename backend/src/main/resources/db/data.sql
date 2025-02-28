-- 清除现有数据
DELETE FROM sys_user_role;
DELETE FROM sys_role_permission;
DELETE FROM sys_user;
DELETE FROM sys_role;
DELETE FROM sys_permission;

-- 创建权限
INSERT INTO sys_permission (id, name, code, type, status, create_time, update_time, deleted) VALUES
(1, '用户查询', 'system:user:query', 1, 0, NOW(), NOW(), false),
(2, '用户新增', 'system:user:add', 1, 0, NOW(), NOW(), false),
(3, '用户编辑', 'system:user:edit', 1, 0, NOW(), NOW(), false),
(4, '用户删除', 'system:user:remove', 1, 0, NOW(), NOW(), false),
(5, '用户导出', 'system:user:export', 1, 0, NOW(), NOW(), false),
(6, '重置密码', 'system:user:resetPwd', 1, 0, NOW(), NOW(), false),
(7, '角色查询', 'system:role:query', 1, 0, NOW(), NOW(), false),
(8, '角色新增', 'system:role:add', 1, 0, NOW(), NOW(), false),
(9, '角色编辑', 'system:role:edit', 1, 0, NOW(), NOW(), false),
(10, '角色删除', 'system:role:remove', 1, 0, NOW(), NOW(), false),
(11, '菜单查询', 'system:menu:query', 1, 0, NOW(), NOW(), false),
(12, '菜单新增', 'system:menu:add', 1, 0, NOW(), NOW(), false),
(13, '菜单编辑', 'system:menu:edit', 1, 0, NOW(), NOW(), false),
(14, '菜单删除', 'system:menu:remove', 1, 0, NOW(), NOW(), false);

-- 创建角色
INSERT INTO sys_role (id, name, code, status, data_scope, create_time, update_time, deleted) VALUES
(1, '超级管理员', 'admin', 0, 1, NOW(), NOW(), false),
(2, '普通用户', 'user', 0, 1, NOW(), NOW(), false),
(3, '访客', 'visitor', 0, 1, NOW(), NOW(), false);

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
INSERT INTO sys_user (id, username, password, nickname, email, phone, gender, avatar, status, create_time, update_time, deleted) VALUES
-- 使用 BCrypt 加密的密码，所有用户的初始密码都是 '123456'
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 'admin@example.com', '13800000001', 1, '/avatar/default.jpg', 0, NOW(), NOW(), false),
(2, 'test', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '测试用户', 'test@example.com', '13800000002', 1, '/avatar/default.jpg', 0, NOW(), NOW(), false),
(3, 'guest', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '访客用户', 'guest@example.com', '13800000003', 0, '/avatar/default.jpg', 0, NOW(), NOW(), false);

-- 分配用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), -- admin -> 超级管理员
(2, 2), -- test -> 普通用户
(3, 3); -- guest -> 访客