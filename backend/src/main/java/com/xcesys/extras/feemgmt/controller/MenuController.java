package com.xcesys.extras.feemgmt.controller;

import com.xcesys.extras.feemgmt.common.Result;
import com.xcesys.extras.feemgmt.dto.MenuDto;
import com.xcesys.extras.feemgmt.entity.Menu;
import com.xcesys.extras.feemgmt.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 获取菜单树
     *
     * @return 菜单树结构
     */
    @GetMapping({"/tree", "list"})
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<Menu>> getMenuTree() {

        List<Menu> menuTree = menuService.findMenuTree();
        return Result.success(menuTree);
    }

    /**
     * 获取当前用户菜单树
     *
     * @return 当前用户的菜单树结构
     */
    @GetMapping("/user")
    public Result<List<Menu>> getUserMenuTree(@RequestParam Long userId) {
        List<Menu> userMenuTree = menuService.findUserMenuTree(userId);
        return Result.success(userMenuTree);
    }

    /**
     * 获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Result<Menu> getMenuDetail(@PathVariable Long id) {
        Menu menu = menuService.findById(id);
        return Result.success(menu);
    }

    /**
     * 创建菜单
     *
     * @param menuDto 菜单信息
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add')")
    public Result<MenuDto> createMenu(@Valid @RequestBody MenuDto menuDto) {
        // 创建菜单
        Menu menu = new Menu();
        menu.setParentId(menuDto.getParentId());
        menu.setName(menuDto.getName());
        menu.setPath(menuDto.getPath());
        menu.setComponent(menuDto.getComponent());
        menu.setQuery(menuDto.getQuery());
        menu.setIsExternal(menuDto.getIsExternal());
        menu.setIsCache(menuDto.getIsCache());
        menu.setType(menuDto.getType());
        menu.setVisible(menuDto.getVisible());
        menu.setStatus(menuDto.getStatus());
        menu.setPermissionCode(menuDto.getPermissionCode());
        menu.setIcon(menuDto.getIcon());
        menu.setSort(menuDto.getSort());
        menu.setRemark(menuDto.getRemark());

        Menu createdMenu = menuService.createMenu(menu);

        // 转换为DTO
        MenuDto createdMenuDto = convertToDto(createdMenu);

        return Result.success("创建成功", createdMenuDto);
    }

    /**
     * 更新菜单
     *
     * @param id      菜单ID
     * @param menuDto 菜单信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<MenuDto> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuDto menuDto) {
        // 检查菜单是否存在
        Menu existingMenu = menuService.findById(id);

        // 更新菜单
        existingMenu.setParentId(menuDto.getParentId());
        existingMenu.setName(menuDto.getName());
        existingMenu.setPath(menuDto.getPath());
        existingMenu.setComponent(menuDto.getComponent());
        existingMenu.setQuery(menuDto.getQuery());
        existingMenu.setIsExternal(menuDto.getIsExternal());
        existingMenu.setIsCache(menuDto.getIsCache());
        existingMenu.setType(menuDto.getType());
        existingMenu.setVisible(menuDto.getVisible());
        existingMenu.setStatus(menuDto.getStatus());
        existingMenu.setPermissionCode(menuDto.getPermissionCode());
        existingMenu.setIcon(menuDto.getIcon());
        existingMenu.setSort(menuDto.getSort());
        existingMenu.setRemark(menuDto.getRemark());

        Menu updatedMenu = menuService.updateMenu(existingMenu);

        // 转换为DTO
        MenuDto updatedMenuDto = convertToDto(updatedMenu);

        return Result.success("更新成功", updatedMenuDto);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result<String> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success("删除成功");
    }

    /**
     * 更新菜单状态
     *
     * @param id     菜单ID
     * @param status 状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        menuService.updateStatus(id, status);
        return Result.success("更新状态成功");
    }

    /**
     * 根据类型获取菜单列表
     *
     * @param type 菜单类型（M目录 C菜单 F按钮）
     * @return 菜单列表
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<Menu>> getMenusByType(@PathVariable String type) {
        List<Menu> menus = menuService.findByType(type);
        return Result.success(menus);
    }

    /**
     * 获取角色菜单树
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority('system:menu:list')")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        List<Long> menuIds = menuService.findMenusByRoleId(roleId).stream()
                .map(Menu::getId)
                .collect(Collectors.toList());
        return Result.success(menuIds);
    }
    
    /**
     * 将菜单实体转换为DTO
     *
     * @param menu 菜单实体
     * @return 菜单DTO
     */
    private MenuDto convertToDto(Menu menu) {
        MenuDto dto = new MenuDto();
        dto.setId(menu.getId());
        dto.setParentId(menu.getParentId());
        dto.setName(menu.getName());
        dto.setPath(menu.getPath());
        dto.setComponent(menu.getComponent());
        dto.setQuery(menu.getQuery());
        dto.setIsExternal(menu.getIsExternal());
        dto.setIsCache(menu.getIsCache());
        dto.setType(menu.getType());
        dto.setVisible(menu.getVisible());
        dto.setStatus(menu.getStatus());
        dto.setPermissionCode(menu.getPermissionCode());
        dto.setIcon(menu.getIcon());
        dto.setSort(menu.getSort());
        dto.setRemark(menu.getRemark());
        return dto;
    }
}