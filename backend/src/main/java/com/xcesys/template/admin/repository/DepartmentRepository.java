package com.xcesys.template.admin.repository;

import com.xcesys.template.admin.entity.Department;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 部门数据访问接口
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

  /**
   * 根据父ID查询部门列表
   *
   * @param parentId 父ID
   * @param sort     排序
   * @return 部门列表
   */
  List<Department> findByParentId(Long parentId, Sort sort);

  /**
   * 查询顶级部门
   *
   * @param sort 排序
   * @return 部门列表
   */
  List<Department> findByParentIdIsNull(Sort sort);

  /**
   * 根据部门名称查询部门
   *
   * @param name 部门名称
   * @return 部门对象
   */
  Optional<Department> findByName(String name);

  /**
   * 根据部门名称和父ID查询部门
   *
   * @param name     部门名称
   * @param parentId 父ID
   * @return 部门对象
   */
  Optional<Department> findByNameAndParentId(String name, Long parentId);

  /**
   * 判断部门名称是否存在
   *
   * @param name 部门名称
   * @return 是否存在
   */
  boolean existsByName(String name);

  /**
   * 判断部门名称和父ID是否存在
   *
   * @param name     部门名称
   * @param parentId 父ID
   * @return 是否存在
   */
  boolean existsByNameAndParentId(String name, Long parentId);

  /**
   * 根据部门状态查询部门列表
   *
   * @param enabled 部门状态
   * @return 部门列表
   */
  List<Department> findByEnabled(Boolean enabled);

  /**
   * 查询指定部门及其所有子部门
   *
   * @param deptId 部门ID
   * @return 部门列表
   */
  @Query("SELECT d FROM Department d WHERE d.id = :deptId OR d.parentId = :deptId")
  List<Department> findDeptAndChildren(@Param("deptId") Long deptId);

  /**
   * 递归查询所有子部门
   * <p>
   * 注：此查询需要数据库支持递归查询，如MySQL 8.0+的WITH RECURSIVE语法
   *
   * @param deptId 部门ID
   * @return 子部门列表ID
   */
  @Query(nativeQuery = true, value =
      "WITH RECURSIVE dept_tree AS (" +
          "  SELECT id, name, parent_id FROM sys_department WHERE id = :deptId " +
          "  UNION ALL " +
          "  SELECT d.id, d.name, d.parent_id FROM sys_department d " +
          "  JOIN dept_tree dt ON dt.id = d.parent_id" +
          ") " +
          "SELECT id FROM dept_tree WHERE id != :deptId")
  List<Long> findChildrenIds(@Param("deptId") Long deptId);
}