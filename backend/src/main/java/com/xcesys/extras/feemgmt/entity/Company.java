package com.xcesys.extras.feemgmt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 公司/租户实体类
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
// 移除以下两行
// @SQLDelete(sql = "UPDATE sys_company SET deleted = true WHERE id = ? and version = ?")
// @Where(clause = "deleted = false")
@Table(name = "sys_company", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"}), @UniqueConstraint(columnNames = {"domain"})})
public class Company extends BaseEntity {

  /**
   * 公司名称
   */
  @NotBlank(message = "公司名称不能为空")
  @Size(max = 100, message = "公司名称长度不能超过100个字符")
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  /**
   * 公司编码
   */
  @NotBlank(message = "公司编码不能为空")
  @Size(max = 50, message = "公司编码长度不能超过50个字符")
  @Column(name = "code", unique = true, nullable = false, length = 50)
  private String code;

  /**
   * 联系人
   */
  @Size(max = 50, message = "联系人长度不能超过50个字符")
  @Column(name = "contact_name", length = 50)
  private String contactName;

  /**
   * 联系电话
   */
  @Size(max = 20, message = "联系电话长度不能超过20个字符")
  @Column(name = "contact_phone", length = 20)
  private String contactPhone;

  /**
   * 邮箱
   */
  @Size(max = 100, message = "邮箱长度不能超过100个字符")
  @Column(name = "email", length = 100)
  private String email;

  /**
   * 地址
   */
  @Size(max = 200, message = "地址长度不能超过200个字符")
  @Column(name = "address", length = 200)
  private String address;

  /**
   * 公司logo
   */
  @Column(name = "logo")
  private String logo;

  /**
   * 公司域名
   */
  @Size(max = 100, message = "域名长度不能超过100个字符")
  @Column(name = "domain", length = 100)
  private String domain;

  /**
   * 过期时间
   */
  @Column(name = "expire_time")
  private LocalDateTime expireTime;

  /**
   * 状态（true 启用 false 禁用）
   */
  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  /**
   * 备注
   */
  @Size(max = 500, message = "备注长度不能超过500个字符")
  @Column(name = "remark", length = 500)
  private String remark;
}