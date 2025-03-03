package com.xcesys.extras.feemgmt.service.impl;

import com.xcesys.extras.feemgmt.entity.Permission;
import com.xcesys.extras.feemgmt.exception.BusinessException;
import com.xcesys.extras.feemgmt.repository.PermissionRepository;
import com.xcesys.extras.feemgmt.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 权限服务实现类
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final PermissionRepository permissionRepository;

  @Override
  public void deleteAll(List<Long> ids) {
    this.permissionRepository.deleteAllById(ids);
  }

  @Override
  public Permission findById(Long id) {
    return permissionRepository.findById(id)
        .orElseThrow(() -> BusinessException.notFoundError("权限不存在: ID=" + id));
  }

  @Override
  public Optional<Permission> findByCode(String code) {
    return permissionRepository.findByCode(code);
  }

  @Override
  public Page<Permission> findPermissions(String name, Integer type, Pageable pageable) {
    if (StringUtils.hasText(name)) {
      return permissionRepository.findByNameContaining(name, pageable);
    } else {
      return permissionRepository.findAll(pageable);
    }
  }

  @Override
  public Page<Permission> findAllPermissions(Pageable pageable) {
    return permissionRepository.findAll(pageable);
  }

  @Override
  public List<Permission> findByType(Integer type) {
    return permissionRepository.findByType(type);
  }

  @Override
  public Set<Permission> findByRoleId(Long roleId) {
    return permissionRepository.findByRoleId(roleId);
  }

  @Override
  public Set<Permission> findByUserId(Long userId) {
    return permissionRepository.findByUserId(userId);
  }

  @Override
  @Transactional
  public Permission createPermission(Permission permission) {
    // 检查权限编码是否已存在
    if (isCodeExists(permission.getCode())) {
      throw BusinessException.conflictError("权限编码已存在: " + permission.getCode());
    }

    // 设置默认状态为启用
    if (permission.getStatus() == null) {
      permission.setStatus(0);
    }

    return permissionRepository.save(permission);
  }

  @Override
  @Transactional
  public Permission updatePermission(Permission permission) {
    Permission existingPermission = findById(permission.getId());

    // 检查权限编码是否已存在
    if (!existingPermission.getCode().equals(permission.getCode()) && isCodeExists(permission.getCode())) {
      throw BusinessException.conflictError("权限编码已存在: " + permission.getCode());
    }

    return permissionRepository.save(permission);
  }

  @Override
  @Transactional
  public void deletePermission(Long id) {
    Permission permission = findById(id);

    // 检查是否存在关联的角色
    if (!permission.getRoles().isEmpty()) {
      throw BusinessException.validationError("该权限已分配给角色，无法删除");
    }

    permissionRepository.delete(permission);
  }

  @Override
  @Transactional
  public void updateStatus(Long id, Integer status) {
    Permission permission = findById(id);
    permission.setStatus(status);
    permissionRepository.save(permission);
  }

  @Override
  public boolean isCodeExists(String code) {
    return permissionRepository.existsByCode(code);
  }

  @Override
  public boolean hasPermission(Long userId, List<String> permissionCodes, boolean requireAll) {
    // 获取用户拥有的所有权限
    Set<Permission> userPermissions = findByUserId(userId);

    // 提取权限编码
    Set<String> userPermissionCodes = userPermissions.stream()
        .map(Permission::getCode)
        .collect(java.util.stream.Collectors.toSet());

    // 校验用户是否有管理员权限
    boolean isAdmin = userPermissionCodes.contains("admin");
    if (isAdmin) {
      return true;
    }

    // 根据requireAll参数判断权限
    if (requireAll) {
      // 需要满足所有权限
      return permissionCodes.stream().allMatch(userPermissionCodes::contains);
    } else {
      // 只需满足任一权限
      return permissionCodes.stream().anyMatch(userPermissionCodes::contains);
    }
  }
}