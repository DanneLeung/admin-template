package com.xcesys.extras.feemgmt.service;

import com.xcesys.extras.feemgmt.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 部门管理服务接口
 */
public interface DepartmentService {

    /**
     * 根据ID查询部门
     */
    Department findById(Long id);

    /**
     * 获取部门树形结构
     */
    List<Department> getDepartmentTree(String name, Boolean enabled);

    /**
     * 分页查询部门列表
     */
    Page<Department> findDepartments(String name, Boolean enabled, Pageable pageable);

    /**
     * 创建部门
     */
    Department createDepartment(Department department);

    /**
     * 更新部门
     */
    Department updateDepartment(Department department);

    /**
     * 删除部门
     */
    void deleteDepartment(Long id);

    /**
     * 更新部门状态
     */
    void updateEnabled(Long id, Boolean enabled);

    /**
     * 检查部门名称是否存在
     */
    boolean isNameExists(String name, Long parentId);
}