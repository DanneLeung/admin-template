package com.xcesys.template.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sys_user")
// 移除以下软删除注解
// @SQLDelete(sql = "UPDATE sys_user SET deleted = true WHERE id = ? and version = ?")
// @SQLRestriction(value = "deleted = false")
public class User extends TenantBaseEntity {

  /**
   * 头像地址
   */
  @Column(name = "avatar")
  private String avatar;

  /**
   * 用户所属部门
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dept_id", insertable = false, updatable = false)
  private Department department;

  @Column(name = "dept_id")
  private Long departmentId;
  /**
   * 用户邮箱
   */
  @Email(message = "邮箱格式不正确")
  @Size(max = 50, message = "邮箱长度不能超过50个字符")
  @Column(name = "email", length = 50)
  private String email;
  /**
   * 用户性别（0男 1女 2未知）
   */
  @Column(name = "gender")
  private Integer gender = 2;
  /**
   * 账户锁定时间
   */
  @Column(name = "lock_time")
  private LocalDateTime lockTime;
  /**
   * 登录失败次数
   */
  @Column(name = "login_fail_count")
  private Integer loginFailCount = 0;
  /**
   * 最后登录IP
   */
  @Column(name = "login_ip")
  private String loginIp;
  /**
   * 最后登录时间
   */
  @Column(name = "login_time")
  private LocalDateTime loginTime;
  /**
   * 用户昵称
   */
  @Size(max = 30, message = "用户昵称长度不能超过30个字符")
  @Column(name = "nickname", length = 30)
  private String nickname;
  /**
   * 密码
   */
  @NotBlank(message = "密码不能为空")
  @Column(name = "password", nullable = false)
  private String password;
  /**
   * 手机号码
   */
  @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
  @Column(name = "phone", length = 11)
  private String phone;
  /**
   * 用户角色列表
   */
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "sys_user_role",
      joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
  )
  private Set<Role> roles = new HashSet<>();
  /**
   * 帐号状态（true 启用 false 禁用）
   */
  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;
  /**
   * 用户名
   */
  @NotBlank(message = "用户名不能为空")
  @Size(min = 3, max = 20, message = "用户名长度必须在3到20个字符之间")
  @Column(name = "username", unique = true, nullable = false, length = 20)
  private String username;
}