package com.xcesys.template.admin.repository;

import com.xcesys.template.admin.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 权限数据访问接口
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * 根据权限编码查询权限
     *
     * @param code 权限编码
     * @return 权限对象
     */
    Optional<Permission> findByCode(String code);
    
    /**
     * 判断权限编码是否存在
     *
     * @param code 权限编码
     * @return 是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 根据权限类型查询权限列表
     *
     * @param type 权限类型
     * @return 权限列表
     */
    List<Permission> findByType(Integer type);
    
    /**
     * 根据权限名称模糊查询权限
     *
     * @param name 权限名称
     * @param pageable 分页参数
     * @return 权限分页列表
     */
    Page<Permission> findByNameContaining(String name, Pageable pageable);
    
    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    Set<Permission> findByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r JOIN r.users u WHERE u.id = :userId")
    Set<Permission> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据权限类型和状态查询权限列表
     *
     * @param type 权限类型
     * @param enabled 权限状态
     * @return 权限列表
     */
    List<Permission> findByTypeAndEnabled(Integer type, Boolean enabled);
}