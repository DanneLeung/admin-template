package com.xcesys.template.admin.service;

import com.xcesys.template.admin.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色对象
     */
    Role findById(Long id);

    /**
     * 根据角色编码查询角色
     *
     * @param code 角色编码
     * @return 角色对象（可能为空）
     */
    Optional<Role> findByCode(String code);

    /**
     * 根据角色名称查询角色
     *
     * @param name 角色名称
     * @return 角色对象（可能为空）
     */
    Optional<Role> findByName(String name);

    /**
     * 分页查询角色列表
     *
     * @param name     角色名称（模糊查询）
     * @param enabled   角色状态
     * @param pageable 分页参数
     * @return 角色分页列表
     */
    Page<Role> findRoles(String name, Boolean enabled, Pageable pageable);

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    List<Role> findAllRoles();

    /**
     * 查询用户拥有的角色列表
     *
     * @param userId 用户ID
     * @return 角色集合
     */
    Set<Role> findRolesByUserId(Long userId);

    /**
     * 创建角色
     *
     * @param role 角色对象
     * @return 创建后的角色对象
     */
    Role createRole(Role role);

    /**
     * 更新角色
     *
     * @param role 角色对象
     * @return 更新后的角色对象
     */
    Role updateRole(Role role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void deleteRole(Long id);

    /**
     * 更新角色状态
     *
     * @param id     角色ID
     * @param enabled 状态
     */
    void updateEnabled(Long id, Boolean enabled);

    /**
     * 为角色分配权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID集合
     */
    void assignPermissions(Long roleId, Set<Long> permissionIds);

    /**
     * 为角色分配菜单
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID集合
     */
    void assignMenus(Long roleId, Set<Long> menuIds);

    /**
     * 判断角色编码是否存在
     *
     * @param code 角色编码
     * @return 是否存在
     */
    boolean isCodeExists(String code);

    /**
     * 判断角色名称是否存在
     *
     * @param name 角色名称
     * @return 是否存在
     */
    boolean isNameExists(String name);
}