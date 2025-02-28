package com.xcesys.extras.feemgmt.security;

import com.xcesys.extras.feemgmt.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security用户详情实现类
 */
@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {
  @Serial
  private static final long serialVersionUID = 1L;

  private Long id;
  private String username;
  @JsonIgnore
  private String password;
  private Integer status;
  private Collection<? extends GrantedAuthority> authorities;

  /**
   * 从User实体创建UserDetailsImpl对象
   *
   * @param user        用户实体
   * @param permissions 用户权限列表
   * @return UserDetailsImpl对象
   */
  public static UserDetailsImpl build(User user, Set<String> permissions) {
    List<SimpleGrantedAuthority> authorities = permissions.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    return new UserDetailsImpl(
        user.getId(),
        user.getUsername(),
        user.getPassword(),
        user.getStatus(),
        authorities);
  }


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // 0: 正常, 1: 停用
    return !Objects.equals(status, 1);
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    // 0: 正常, 1: 停用
    return Objects.equals(status, 0);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}