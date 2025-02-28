package com.xcesys.extras.feemgmt.service;

import com.xcesys.extras.feemgmt.entity.Menu;

import java.util.List;
import java.util.Set;

/**
 * 菜单服务接口
 */
public interface MenuService {

    /**
     * 根据ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单对象
     */
    Menu findById(Long id);

    /**
     * 查询菜单树
     *
     * @return 菜单树结构
     */
    List<Menu> findMenuTree();
    
    /**
     * 查询用户菜单树
     *
     * @param userId 用户ID
     * @return 菜单树结构
     */
    List<Menu> findUserMenuTree(Long userId);

    /**
     * 创建菜单
     *
     * @param menu 菜单对象
     * @return 创建后的菜单对象
     */
    Menu createMenu(Menu menu);

    /**
     * 更新菜单
     *
     * @param menu 菜单对象
     * @return 更新后的菜单对象
     */
    Menu updateMenu(Menu menu);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    void deleteMenu(Long id);

    /**
     * 查询菜单及其子菜单
     *
     * @param id 菜单ID
     * @return 菜单及其子菜单列表
     */
    List<Menu> findMenuWithChildren(Long id);

    /**
     * 查询角色拥有的菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单集合
     */
    Set<Menu> findMenusByRoleId(Long roleId);

    /**
     * 查询用户拥有的菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> findMenusByUserId(Long userId);

    /**
     * 根据菜单类型查询菜单列表
     *
     * @param type 菜单类型
     * @return 菜单列表
     */
    List<Menu> findByType(String type);
    
    /**
     * 判断菜单是否有子菜单
     *
     * @param id 菜单ID
     * @return 是否有子菜单
     */
    boolean hasChildMenu(Long id);
    
    /**
     * 更新菜单状态
     *
     * @param id     菜单ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);
}