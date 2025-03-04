package com.xcesys.extras.feemgmt.service.impl;

import com.xcesys.extras.feemgmt.entity.Menu;
import com.xcesys.extras.feemgmt.entity.Permission;
import com.xcesys.extras.feemgmt.entity.Role;
import com.xcesys.extras.feemgmt.exception.BusinessException;
import com.xcesys.extras.feemgmt.repository.MenuRepository;
import com.xcesys.extras.feemgmt.repository.PermissionRepository;
import com.xcesys.extras.feemgmt.repository.RoleRepository;
import com.xcesys.extras.feemgmt.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MenuRepository menuRepository;

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFoundError("角色不存在: ID=" + id));
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return roleRepository.findByCode(code);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Page<Role> findRoles(String name, Boolean enabled, Pageable pageable) {
        if (StringUtils.hasText(name) && enabled != null) {
            return roleRepository.findByNameContainingAndEnabled(name, enabled, pageable);
        } else if (StringUtils.hasText(name)) {
            return roleRepository.findByNameContaining(name, pageable);
        } else if (enabled != null) {
            return roleRepository.findAll(pageable);
        } else {
            return roleRepository.findAll(pageable);
        }
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Set<Role> findRolesByUserId(Long userId) {
        return roleRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Role createRole(Role role) {
        // 检查角色编码是否已存在
        if (isCodeExists(role.getCode())) {
            throw BusinessException.conflictError("角色编码已存在: " + role.getCode());
        }

        // 检查角色名称是否已存在
        if (isNameExists(role.getName())) {
            throw BusinessException.conflictError("角色名称已存在: " + role.getName());
        }

        // 设置默认状态为启用
        if (role.getEnabled() == null) {
            role.setEnabled(true);
        }

        // 设置默认数据范围
        if (role.getDataScope() == null) {
            role.setDataScope(1);
        }

        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role updateRole(Role role) {
        Role existingRole = findById(role.getId());

        // 检查角色编码是否已存在
        if (!existingRole.getCode().equals(role.getCode()) && isCodeExists(role.getCode())) {
            throw BusinessException.conflictError("角色编码已存在: " + role.getCode());
        }

        // 检查角色名称是否已存在
        if (!existingRole.getName().equals(role.getName()) && isNameExists(role.getName())) {
            throw BusinessException.conflictError("角色名称已存在: " + role.getName());
        }

        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = findById(id);
        
        // 检查角色是否是系统内置角色
        if ("admin".equalsIgnoreCase(role.getCode())) {
            throw BusinessException.validationError("不能删除系统内置管理员角色");
        }
        
        roleRepository.delete(role);
    }

    @Override
    @Transactional
    public void updateEnabled(Long id, Boolean enabled) {
        Role role = findById(id);
        
        // 检查角色是否是系统内置角色
        if ("admin".equalsIgnoreCase(role.getCode()) && !enabled) {
            throw BusinessException.validationError("不能停用系统内置管理员角色");
        }
        
        role.setEnabled(enabled);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, Set<Long> permissionIds) {
        Role role = findById(roleId);
        
        // 清空原有权限
        role.getPermissions().clear();
        
        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Permission> permissions = permissionIds.stream()
                    .map(permissionId -> permissionRepository.findById(permissionId)
                            .orElseThrow(() -> BusinessException.notFoundError("权限不存在: ID=" + permissionId)))
                    .collect(Collectors.toSet());
            
            role.getPermissions().addAll(permissions);
        }
        
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, Set<Long> menuIds) {
        Role role = findById(roleId);
        
        // 清空原有菜单
        role.getMenus().clear();
        
        // 添加新菜单
        if (menuIds != null && !menuIds.isEmpty()) {
            Set<Menu> menus = menuIds.stream()
                    .map(menuId -> menuRepository.findById(menuId)
                            .orElseThrow(() -> BusinessException.notFoundError("菜单不存在: ID=" + menuId)))
                    .collect(Collectors.toSet());
            
            role.getMenus().addAll(menus);
        }
        
        roleRepository.save(role);
    }

    @Override
    public boolean isCodeExists(String code) {
        return roleRepository.existsByCode(code);
    }

    @Override
    public boolean isNameExists(String name) {
        return roleRepository.findByName(name).isPresent();
    }
}