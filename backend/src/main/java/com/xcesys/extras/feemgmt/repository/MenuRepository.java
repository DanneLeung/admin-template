package com.xcesys.extras.feemgmt.repository;

import com.xcesys.extras.feemgmt.entity.Menu;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 菜单数据访问接口
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    /**
     * 根据父ID查询菜单列表
     *
     * @param parentId 父ID
     * @param sort 排序
     * @return 菜单列表
     */
    List<Menu> findByParentId(Long parentId, Sort sort);
    
    /**
     * 查询顶级菜单
     *
     * @param sort 排序
     * @return 菜单列表
     */
    List<Menu> findByParentIdIsNull(Sort sort);
    
    /**
     * 根据菜单类型查询菜单列表
     *
     * @param type 菜单类型
     * @return 菜单列表
     */
    List<Menu> findByType(String type);
    
    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    @Query("SELECT m FROM Menu m JOIN m.roles r WHERE r.id = :roleId")
    Set<Menu> findByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Query("SELECT DISTINCT m FROM Menu m JOIN m.roles r JOIN r.users u WHERE u.id = :userId AND m.visible = true AND m.enabled = true ORDER BY m.sort ASC")
    List<Menu> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据权限编码查询菜单
     *
     * @param permissionCode 权限编码
     * @return 菜单列表
     */
    List<Menu> findByPermissionCode(String permissionCode);
    
    /**
     * 查询所有可见的菜单
     *
     * @param sort 排序
     * @return 菜单列表
     */
    List<Menu> findByVisibleTrue(Sort sort);
}