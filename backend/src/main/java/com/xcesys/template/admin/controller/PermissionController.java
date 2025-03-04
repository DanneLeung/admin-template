package com.xcesys.template.admin.controller;

import com.xcesys.template.admin.common.Result;
import com.xcesys.template.admin.entity.Permission;
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
  public Result<Permission> create(@RequestBody Permission permission) {
    return Result.success(permissionService.updatePermission(permission));
  }

  @PutMapping
  @PreAuthorize("hasAuthority('system:permission:edit')")
  public Result<Permission> update(@RequestBody Permission permission) {
    return Result.success(permissionService.updatePermission(permission));
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