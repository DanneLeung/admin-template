package com.xcesys.template.admin.controller;

import com.xcesys.template.admin.common.Result;
import com.xcesys.template.admin.dto.PermissionDto;
import com.xcesys.template.admin.entity.Permission;
import com.xcesys.template.admin.mapper.PermissionMapper;
import com.xcesys.template.admin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

  private final PermissionService permissionService;
  private final PermissionMapper permissionMapper;

  @GetMapping
  @PreAuthorize("hasAuthority('system:permission:list')")
  public Result<Page<Permission>> list(Pageable pageable) {
    return Result.success(permissionService.findAllPermissions(pageable));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('system:permission:query')")
  public Result<Permission> detail(@PathVariable Long id) {
    return Result.success(permissionService.findById(id));
  }

  @PostMapping
  @PreAuthorize("hasAuthority('system:permission:add')")
  public Result<PermissionDto> create(@RequestBody PermissionDto permissionDto) {
    Permission permission = new Permission();
    permissionMapper.toEntity(permission, permissionDto);
    Permission updatedPermission = permissionService.updatePermission(permission);
    return Result.success(permissionMapper.toDto(updatedPermission));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('system:permission:edit')")
  public Result<PermissionDto> update(@PathVariable Long id, @RequestBody PermissionDto permissionDto) {
    Permission permission = permissionService.findById(id);
    permissionMapper.toEntity(permission, permissionDto);
    Permission updatedPermission = permissionService.updatePermission(permission);
    return Result.success(permissionMapper.toDto(updatedPermission));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('system:permission:delete')")
  public Result<String> delete(@PathVariable Long id) {
    permissionService.deletePermission(id);
    return Result.success("删除成功");
  }

  @DeleteMapping("/batch")
  @PreAuthorize("hasAuthority('system:permission:delete')")
  public Result<String> batchDelete(@RequestBody List<Long> ids) {
    permissionService.deleteAll(ids);
    return Result.success("删除成功");
  }

  @PutMapping("/status")
  @PreAuthorize("hasAuthority('system:permission:edit')")
  public Result<String> updateStatus(@RequestParam("id") Long id, @RequestParam("enabled") Boolean enabled) {
    permissionService.updateEnabled(id, enabled);
    return Result.success("更新状态成功");
  }
}