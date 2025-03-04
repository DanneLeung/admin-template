package com.xcesys.template.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门实体类
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sys_department")
// 移除以下两个注解
// @SQLDelete(sql = "UPDATE sys_department SET deleted = true WHERE id = ? and version = ?")
// @Where(clause = "deleted = false")
public class Department extends TenantBaseEntity {

  /**
   * 部门名称
   */
  @NotBlank(message = "部门名称不能为空")
  @Size(max = 50, message = "部门名称长度不能超过50个字符")
  @Column(name = "name", nullable = false)
  private String name;

  /**
   * 父部门ID
   */
  @Column(name = "parent_id")
  private Long parentId;

  /**
   * 显示顺序
   */
  @Column(name = "sort")
  private Integer sort;

  /**
   * 负责人
   */
  @Size(max = 20, message = "负责人长度不能超过20个字符")
  @Column(name = "leader")
  private String leader;

  /**
   * 联系电话
   */
  @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
  @Column(name = "phone")
  private String phone;

  /**
   * 邮箱
   */
  @Email(message = "邮箱格式不正确")
  @Size(max = 50, message = "邮箱长度不能超过50个字符")
  @Column(name = "email")
  private String email;

  /**
   * 部门状态（true 启用 false 禁用）
   */
  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  /**
   * 父部门
   */
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", insertable = false, updatable = false)
  private Department parent;

  /**
   * 子部门
   */
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
  @OrderBy("sort asc")
  private List<Department> children = new ArrayList<>();

  /**
   * 部门下的用户
   */
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "department")
  private List<User> users = new ArrayList<>();

}
