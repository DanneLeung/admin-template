package com.xcesys.template.admin.controller;

import com.xcesys.template.admin.common.Result;
import com.xcesys.template.admin.dto.DepartmentDto;
import com.xcesys.template.admin.entity.Department;
import com.xcesys.template.admin.mapper.DepartmentMapper;
import com.xcesys.template.admin.service.DepartmentService;
import jakarta.validation.Valid;
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
  private final DepartmentMapper departmentMapper;

  /**
   * 获取部门详情
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('system:department:list')")
  public Result<DepartmentDto> getDepartment(@PathVariable Long id) {
    Department department = departmentService.findById(id);
    return Result.success(departmentMapper.toDto(department));
  }

  /**
   * 获取部门树形结构
   */
  @GetMapping({"/tree"})
  @PreAuthorize("hasAuthority('system:department:list')")
  public Result<List<DepartmentDto>> getDepartmentTree(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean enabled) {
    List<Department> tree = departmentService.getDepartmentTree(name, enabled);
    return Result.success(tree.stream().map(departmentMapper::toDto).toList());
  }

  /**
   * 分页查询部门列表
   */
  @GetMapping
  @PreAuthorize("hasAuthority('system:department:list')")
  public Result<Page<DepartmentDto>> listDepartments(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean enabled,
      Pageable pageable) {
    Page<Department> page = departmentService.findDepartments(name, enabled, pageable);
    return Result.success(page.map(departmentMapper::toDto));
  }

  /**
   * 创建部门
   */
  @PostMapping
  @PreAuthorize("hasAuthority('system:department:add')")
  public Result<DepartmentDto> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
    Department department = new Department();
    departmentMapper.toEntity(department, departmentDto);
    Department createdDepartment = departmentService.createDepartment(department);
    return Result.success("创建成功", departmentMapper.toDto(createdDepartment));
  }

  /**
   * 更新部门
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('system:department:edit')")
  public Result<DepartmentDto> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentDto departmentDto) {
    Department existingDepartment = departmentService.findById(id);
    departmentMapper.toEntity(existingDepartment, departmentDto);
    Department updatedDepartment = departmentService.updateDepartment(existingDepartment);
    return Result.success("更新成功", departmentMapper.toDto(updatedDepartment));
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