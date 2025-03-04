package com.xcesys.extras.feemgmt.service.impl;

import com.xcesys.extras.feemgmt.entity.Department;
import com.xcesys.extras.feemgmt.exception.BusinessException;
import com.xcesys.extras.feemgmt.repository.DepartmentRepository;
import com.xcesys.extras.feemgmt.service.DepartmentService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 部门管理服务实现类
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepository departmentRepository;

  @Override
  public Department findById(Long id) {
    return departmentRepository.findById(id).orElseThrow(() -> new BusinessException("部门不存在"));
  }

  @Override
  public List<Department> getDepartmentTree(String name, Boolean enabled) {
    // 查询所有部门
    List<Department> allDepts = departmentRepository.findAll((Specification<Department>) (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (StringUtils.isNotBlank(name)) {
        predicates.add(cb.like(root.get("name"), "%" + name + "%"));
      }
      if (enabled != null) {
        predicates.add(cb.equal(root.get("enabled"), enabled));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    });

    // 构建树形结构
    return buildDepartmentTree(allDepts);
  }

  @Override
  public Page<Department> findDepartments(String name, Boolean enabled, Pageable pageable) {
    return departmentRepository.findAll((Specification<Department>) (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (StringUtils.isNotBlank(name)) {
        predicates.add(cb.like(root.get("name"), "%" + name + "%"));
      }
      if (enabled != null) {
        predicates.add(cb.equal(root.get("enabled"), enabled));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    }, pageable);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Department createDepartment(Department department) {
    validateDepartment(department);
    return departmentRepository.save(department);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Department updateDepartment(Department department) {
    Department existingDept = findById(department.getId());
    validateDepartment(department);

    // 更新基本信息
    existingDept.setName(department.getName());
    existingDept.setParentId(department.getParentId());
    existingDept.setSort(department.getSort());
    existingDept.setLeader(department.getLeader());
    existingDept.setPhone(department.getPhone());
    existingDept.setEmail(department.getEmail());
    existingDept.setEnabled(department.getEnabled());

    return departmentRepository.save(existingDept);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteDepartment(Long id) {
    Department department = findById(id);

    // 检查是否存在子部门
    if (!department.getChildren().isEmpty()) {
      throw new BusinessException("存在子部门，无法删除");
    }

    // 检查是否存在关联用户
    if (!department.getUsers().isEmpty()) {
      throw new BusinessException("部门下存在用户，无法删除");
    }

    departmentRepository.delete(department);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateEnabled(Long id, Boolean enabled) {
    Department department = findById(id);
    department.setEnabled(enabled);
    departmentRepository.save(department);
  }

  @Override
  public boolean isNameExists(String name, Long parentId) {
    return departmentRepository.existsByNameAndParentId(name, parentId);
  }

  /**
   * 构建部门树形结构
   */
  private List<Department> buildDepartmentTree(List<Department> departments) {
    // 按parentId分组
    var deptMap = departments.stream().collect(Collectors.groupingBy(dept -> dept.getParentId() == null ? 0L : dept.getParentId()));

    // 获取顶级部门
    var rootDepts = deptMap.get(0L);
    if (rootDepts == null) {
      return new ArrayList<>();
    }

    // 递归设置子部门
    rootDepts.forEach(dept -> setChildren(dept, deptMap));
    return rootDepts;
  }

  /**
   * 递归设置子部门
   */
  private void setChildren(Department dept, java.util.Map<Long, List<Department>> deptMap) {
    List<Department> children = deptMap.get(dept.getId());
    if (children != null) {
      dept.setChildren(children);
      children.forEach(child -> setChildren(child, deptMap));
    }
  }

  /**
   * 验证部门信息
   */
  private void validateDepartment(Department department) {
    // 检查父部门是否存在
    if (department.getParentId() != null) {
      Department parent = findById(department.getParentId());
      if (Objects.equals(parent.getEnabled(), Boolean.FALSE)) {
        throw new BusinessException("父部门已停用，无法添加子部门");
      }
    }

    // 检查部门名称是否重复
    if (department.getId() == null && isNameExists(department.getName(), department.getParentId())) {
      throw new BusinessException("部门名称已存在");
    }
  }
}