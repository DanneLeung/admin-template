package com.xcesys.template.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 用户登录响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  /**
   * 用户ID
   */
  private Long id;

  /**
   * 用户名
   */
  private String username;

  /**
   * 用户昵称
   */
  private String nickname;

  /**
   * 用户头像
   */
  private String avatar;

  /**
   * 认证令牌
   */
  private String token;

  /**
   * 令牌类型
   */
  private String tokenType;

  /**
   * 过期时间（毫秒）
   */
  private Long expiresIn;

  /**
   * 用户角色列表
   */
  private Set<String> roles;

  /**
   * 用户权限列表
   */
  private Set<String> permissions;

  /**
   * 公司ID
   */
  private Long companyId;
}