package com.xcesys.extras.feemgmt.repository;

import com.xcesys.extras.feemgmt.entity.Role;
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
 * 角色数据访问接口
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根据角色编码查询角色
     *
     * @param code 角色编码
     * @return 角色对象
     */
    Optional<Role> findByCode(String code);
    
    /**
     * 判断角色编码是否存在
     *
     * @param code 角色编码
     * @return 是否存在
     */
    boolean existsByCode(String code);
    
    /**
     * 根据角色名称查询角色
     *
     * @param name 角色名称
     * @return 角色对象
     */
    Optional<Role> findByName(String name);
    
    /**
     * 根据用户ID查询用户拥有的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    Set<Role> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据角色名称模糊查询角色
     *
     * @param name 角色名称
     * @param pageable 分页参数
     * @return 角色分页列表
     */
    Page<Role> findByNameContaining(String name, Pageable pageable);
    
    /**
     * 根据角色状态查询角色列表
     *
     * @param status 角色状态
     * @return 角色列表
     */
    List<Role> findByStatus(Integer status);
    
    /**
     * 根据角色名称和状态查询角色列表
     *
     * @param name 角色名称
     * @param status 角色状态
     * @param pageable 分页参数
     * @return 角色分页列表
     */
    Page<Role> findByNameContainingAndStatus(String name, Integer status, Pageable pageable);
}