package com.xcesys.template.admin.controller;

import com.xcesys.template.admin.common.Result;
import com.xcesys.template.admin.dto.RoleDto;
import com.xcesys.template.admin.entity.Role;
import com.xcesys.template.admin.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取角色列表（分页）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param name     角色名称（模糊查询）
     * @param enabled   角色状态
     * @return 角色分页列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<Page<RoleDto>> listRoles(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean enabled) {

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("sort").ascending());
        Page<Role> roles = roleService.findRoles(name, enabled, pageable);

        return Result.success(roles.map(this::convertToDto));
    }

    /**
     * 获取所有角色列表（不分页）
     *
     * @return 角色列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<List<Role>> getAllRoles() {
        List<Role> roles = roleService.findAllRoles();
        return Result.success(roles);
    }

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<Role> getRoleDetail(@PathVariable Long id) {
        Role role = roleService.findById(id);
        return Result.success(role);
    }

    /**
     * 创建角色
     *
     * @param roleDto 角色信息
     * @return 创建结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        // 检查角色编码是否已存在
        if (roleService.isCodeExists(roleDto.getCode())) {
            return Result.error(400, "角色编码已存在");
        }

        // 检查角色名称是否已存在
        if (roleService.isNameExists(roleDto.getName())) {
            return Result.error(400, "角色名称已存在");
        }

        // 创建角色
        Role role = new Role();
        role.setName(roleDto.getName());
        role.setCode(roleDto.getCode());
        role.setSort(roleDto.getSort());
        role.setEnabled(roleDto.getEnabled());
        role.setDataScope(roleDto.getDataScope());
        role.setRemark(roleDto.getRemark());

        Role createdRole = roleService.createRole(role);

        // 如果指定了权限，分配权限
        if (roleDto.getPermissionIds() != null && !roleDto.getPermissionIds().isEmpty()) {
            roleService.assignPermissions(createdRole.getId(), roleDto.getPermissionIds());
        }

        // 如果指定了菜单，分配菜单
        if (roleDto.getMenuIds() != null && !roleDto.getMenuIds().isEmpty()) {
            roleService.assignMenus(createdRole.getId(), roleDto.getMenuIds());
        }

        // 转换为DTO
        RoleDto createdRoleDto = convertToDto(createdRole);

        return Result.success("创建成功", createdRoleDto);
    }

    /**
     * 更新角色
     *
     * @param id      角色ID
     * @param roleDto 角色信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<RoleDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDto roleDto) {
        // 检查角色是否存在
        Role existingRole = roleService.findById(id);

        // 检查角色编码是否已存在
        if (!existingRole.getCode().equals(roleDto.getCode()) && roleService.isCodeExists(roleDto.getCode())) {
            return Result.error(400, "角色编码已存在");
        }

        // 检查角色名称是否已存在
        if (!existingRole.getName().equals(roleDto.getName()) && roleService.isNameExists(roleDto.getName())) {
            return Result.error(400, "角色名称已存在");
        }

        // 更新角色
        existingRole.setName(roleDto.getName());
        existingRole.setCode(roleDto.getCode());
        existingRole.setSort(roleDto.getSort());
        existingRole.setEnabled(roleDto.getEnabled());
        existingRole.setDataScope(roleDto.getDataScope());
        existingRole.setRemark(roleDto.getRemark());

        Role updatedRole = roleService.updateRole(existingRole);

        // 如果指定了权限，更新权限
        if (roleDto.getPermissionIds() != null) {
            roleService.assignPermissions(updatedRole.getId(), roleDto.getPermissionIds());
        }

        // 如果指定了菜单，更新菜单
        if (roleDto.getMenuIds() != null) {
            roleService.assignMenus(updatedRole.getId(), roleDto.getMenuIds());
        }

        // 转换为DTO
        RoleDto updatedRoleDto = convertToDto(updatedRole);

        return Result.success("更新成功", updatedRoleDto);
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<String> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     * @return 删除结果
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<String> batchDeleteRoles(@RequestBody List<Long> ids) {
        ids.forEach(roleService::deleteRole);
        return Result.success("批量删除成功");
    }

    /**
     * 更新角色状态
     *
     * @param id     角色ID
     * @param enabled 状态
     * @return 更新结果
     */
    @PutMapping("/status")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<String> updateStatus(@RequestParam("id") Long id, @RequestParam("enabled") Boolean enabled) {
        roleService.updateEnabled(id, enabled);
        return Result.success("更新状态成功");
    }

    /**
     * 为角色分配权限
     *
     * @param id            角色ID
     * @param permissionIds 权限ID列表
     * @return 分配结果
     */
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<String> assignPermissions(@PathVariable Long id, @RequestBody Set<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.success("分配权限成功");
    }

    /**
     * 为角色分配菜单
     *
     * @param id      角色ID
     * @param menuIds 菜单ID列表
     * @return 分配结果
     */
    @PostMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<String> assignMenus(@PathVariable Long id, @RequestBody Set<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.success("分配菜单成功");
    }

    /**
     * 获取角色拥有的权限ID列表
     *
     * @param id 角色ID
     * @return 权限ID列表
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<Set<Long>> getRolePermissions(@PathVariable Long id) {
        Role role = roleService.findById(id);
        Set<Long> permissionIds = role.getPermissions().stream()
                .map(permission -> permission.getId())
                .collect(Collectors.toSet());
        return Result.success(permissionIds);
    }

    /**
     * 获取角色拥有的菜单ID列表
     *
     * @param id 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<Set<Long>> getRoleMenus(@PathVariable Long id) {
        Role role = roleService.findById(id);
        Set<Long> menuIds = role.getMenus().stream()
                .map(menu -> menu.getId())
                .collect(Collectors.toSet());
        return Result.success(menuIds);
    }

    /**
     * 将角色实体转换为DTO
     *
     * @param role 角色实体
     * @return 角色DTO
     */
    private RoleDto convertToDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setCode(role.getCode());
        dto.setSort(role.getSort());
        dto.setEnabled(role.getEnabled());
        dto.setDataScope(role.getDataScope());
        dto.setRemark(role.getRemark());
        dto.setPermissionIds(role.getPermissions().stream()
                .map(permission -> permission.getId())
                .collect(Collectors.toSet()));
        dto.setMenuIds(role.getMenus().stream()
                .map(menu -> menu.getId())
                .collect(Collectors.toSet()));
        return dto;
    }
}