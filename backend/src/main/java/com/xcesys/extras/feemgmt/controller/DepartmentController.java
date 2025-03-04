package com.xcesys.extras.feemgmt.controller;

import com.xcesys.extras.feemgmt.common.Result;
import com.xcesys.extras.feemgmt.entity.Department;
import com.xcesys.extras.feemgmt.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {

  private final DepartmentService departmentService;

  /**
   * 获取部门详情
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('system:department:list')")
  public Result<Department> getDepartment(@PathVariable Long id) {
    return Result.success(departmentService.findById(id));
  }

  /**
   * 获取部门树形结构
   */
  @GetMapping({"/tree"})
  @PreAuthorize("hasAuthority('system:department:list')")
  public Result<List<Department>> getDepartmentTree(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean enabled) {
    List<Department> tree = departmentService.getDepartmentTree(name, enabled);
    return Result.success(tree);
  }

  /**
   * 分页查询部门列表
   */
  @GetMapping
  @PreAuthorize("hasAuthority('system:department:list')")
  public Result<Page<Department>> listDepartments(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean enabled,
      Pageable pageable) {
    Page<Department> page = departmentService.findDepartments(name, enabled, pageable);
    return Result.success(page);
  }

  /**
   * 创建部门
   */
  @PostMapping
  @PreAuthorize("hasAuthority('system:department:add')")
  public Result<Department> createDepartment(@RequestBody Department department) {
    return Result.success(departmentService.createDepartment(department));
  }

  /**
   * 更新部门
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('system:department:edit')")
  public Result<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
    department.setId(id);
    return Result.success(departmentService.updateDepartment(department));
  }

  /**
   * 删除部门
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('system:department:delete')")
  public Result<Void> deleteDepartment(@PathVariable Long id) {
    departmentService.deleteDepartment(id);
    return Result.success();
  }

  /**
   * 更新部门状态
   */
  @PutMapping("/status")
  @PreAuthorize("hasAuthority('system:department:edit')")
  public Result<String> updateStatus(@RequestParam("id") Long id, @RequestParam("enabled") Boolean enabled) {
    departmentService.updateEnabled(id, enabled);
    return Result.success();
  }

  /**
   * 检查部门名称是否存在
   */
  @GetMapping("/check-name")
  @PreAuthorize("hasAuthority('system:department:list')")
  public Result<Boolean> checkName(
      @RequestParam String name,
      @RequestParam(required = false) Long parentId) {
    return Result.success(departmentService.isNameExists(name, parentId));
  }
}