package com.xcesys.template.admin.service;

import com.xcesys.template.admin.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 权限服务接口
 */
public interface PermissionService {

    void deleteAll(List<Long> ids);

    /**
     * 根据ID查询权限
     *
     * @param id 权限ID
     * @return 权限对象
     */
    Permission findById(Long id);

    /**
     * 根据权限编码查询权限
     *
     * @param code 权限编码
     * @return 权限对象（可能为空）
     */
    Optional<Permission> findByCode(String code);

    /**
     * 分页查询权限列表
     *
     * @param name     权限名称（模糊查询）
     * @param type     权限类型
     * @param pageable 分页参数
     * @return 权限分页列表
     */
    Page<Permission> findPermissions(String name, Integer type, Pageable pageable);

    /**
     * 查询所有权限列表
     *
     * @return 权限列表
     */
    Page<Permission> findAllPermissions(Pageable pageable);

    /**
     * 根据类型查询权限列表
     *
     * @param type 权限类型
     * @return 权限列表
     */
    List<Permission> findByType(Integer type);

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    Set<Permission> findByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<Permission> findByUserId(Long userId);

    /**
     * 创建权限
     *
     * @param permission 权限对象
     * @return 创建后的权限对象
     */
    Permission createPermission(Permission permission);

    /**
     * 更新权限
     *
     * @param permission 权限对象
     * @return 更新后的权限对象
     */
    Permission updatePermission(Permission permission);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 更新权限状态
     *
     * @param id     权限ID
     * @param enabled 状态
     */
    void updateEnabled(Long id, Boolean enabled);

    /**
     * 判断权限编码是否存在
     *
     * @param code 权限编码
     * @return 是否存在
     */
    boolean isCodeExists(String code);

    /**
     * 校验用户是否具有指定权限
     *
     * @param userId          用户ID
     * @param permissionCodes 权限编码列表
     * @param requireAll      是否需要满足所有权限
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, List<String> permissionCodes, boolean requireAll);
}