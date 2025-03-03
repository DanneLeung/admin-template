package com.xcesys.extras.feemgmt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

/**
 * 权限实体类
 */
@Entity
@Table(name = "sys_permission")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE sys_permission SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Permission extends BaseEntity {

  /**
   * 权限名称
   */
  @NotBlank(message = "权限名称不能为空")
  @Size(max = 50, message = "权限名称长度不能超过50个字符")
  @Column(name = "name", nullable = false)
  private String name;

  /**
   * 权限编码（唯一）
   */
  @NotBlank(message = "权限编码不能为空")
  @Size(max = 100, message = "权限编码长度不能超过100个字符")
  @Column(name = "code", nullable = false, unique = true)
  private String code;

  /**
   * 权限类型（1:菜单, 2:按钮, 3:API, 4:数据）
   */
  @Column(name = "type", nullable = false)
  private Integer type;

  /**
   * 权限状态（0正常 1停用）
   */
  @Column(name = "status", nullable = false)
  private Integer status = 0;

  /**
   * 权限描述
   */
  @Size(max = 200, message = "权限描述长度不能超过200个字符")
  @Column(name = "description")
  private String description;

  /**
   * 角色和权限多对多关系（由Role维护）
   */
  @JsonIgnore
  @ManyToMany(mappedBy = "permissions")
  private Set<Role> roles = new HashSet<>();
}