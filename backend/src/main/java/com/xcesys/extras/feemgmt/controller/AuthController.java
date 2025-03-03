package com.xcesys.extras.feemgmt.controller;

import com.xcesys.extras.feemgmt.common.Result;
import com.xcesys.extras.feemgmt.dto.LoginRequest;
import com.xcesys.extras.feemgmt.dto.LoginResponse;
import com.xcesys.extras.feemgmt.dto.UserDto;
import com.xcesys.extras.feemgmt.entity.User;
import com.xcesys.extras.feemgmt.security.JwtTokenProvider;
import com.xcesys.extras.feemgmt.security.UserDetailsImpl;
import com.xcesys.extras.feemgmt.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Set;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;

  /**
   * 用户登录
   *
   * @param loginRequest 登录请求
   * @return 登录响应
   */
  @PostMapping("/login")
  public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    // 认证用户
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
    );

    // 设置认证信息到上下文
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 生成JWT令牌
    String jwt = jwtTokenProvider.createToken(authentication);

    // 获取用户信息
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    // 获取用户角色和权限
    Set<String> roles = userService.getUserRoles(userDetails.getId());
    Set<String> permissions = userService.getUserPermissions(userDetails.getId());

    // 更新用户登录信息
    User user = userService.findById(userDetails.getId());

    // 构造返回数据
    LoginResponse response = LoginResponse.builder()
        .token(jwt)
        .tokenType("Bearer")
        .id(userDetails.getId())
        .companyId(user.getCompanyId())
        .username(userDetails.getUsername())
        .nickname(user.getNickname())
        .avatar(user.getAvatar())
        .roles(roles)
        .permissions(permissions)
        .expiresIn(jwtTokenProvider.getExpirationDateFromToken(jwt).getTime() - new Date().getTime())
        .build();

    return Result.success("登录成功", response);
  }

  /**
   * 用户注册
   *
   * @param userDto 用户信息
   * @return 注册结果
   */
  @PostMapping("/register")
  public Result<UserDto> register(@Valid @RequestBody UserDto userDto) {
    // 检查用户名是否已存在
    if (userService.isUsernameExists(userDto.getUsername())) {
      return Result.error(400, "用户名已存在");
    }

    // 检查邮箱是否已存在
    if (userDto.getEmail() != null && userService.isEmailExists(userDto.getEmail())) {
      return Result.error(400, "邮箱已存在");
    }

    // 检查手机号是否已存在
    if (userDto.getPhone() != null && userService.isPhoneExists(userDto.getPhone())) {
      return Result.error(400, "手机号已存在");
    }

    // 创建用户
    User user = new User();
    user.setUsername(userDto.getUsername());
    user.setPassword(userDto.getPassword());
    user.setNickname(userDto.getNickname());
    user.setEmail(userDto.getEmail());
    user.setPhone(userDto.getPhone());
    user.setGender(userDto.getGender());
    user.setStatus(0); // 默认启用

    User createdUser = userService.createUser(user);

    // 转换为DTO
    UserDto createdUserDto = new UserDto();
    createdUserDto.setId(createdUser.getId());
    createdUserDto.setUsername(createdUser.getUsername());
    createdUserDto.setNickname(createdUser.getNickname());
    createdUserDto.setEmail(createdUser.getEmail());
    createdUserDto.setPhone(createdUser.getPhone());
    createdUserDto.setGender(createdUser.getGender());
    createdUserDto.setStatus(createdUser.getStatus());

    return Result.success("注册成功", createdUserDto);
  }

  /**
   * 退出登录
   *
   * @return 退出结果
   */
  @PostMapping("/logout")
  public Result<String> logout() {
    // JWT是无状态的，客户端只需要删除JWT即可
    // 这里可以添加令牌黑名单或撤销令牌的逻辑（需要Redis支持）
    return Result.success("退出成功");
  }
}