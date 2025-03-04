package com.xcesys.template.admin.controller;

import com.xcesys.template.admin.common.Result;
import com.xcesys.template.admin.dto.UserDto;
import com.xcesys.template.admin.entity.User;
import com.xcesys.template.admin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * 获取当前登录用户信息
   *
   * @param authentication 认证信息
   * @return 当前用户信息
   */
  @GetMapping("/info")
  public Result<Map<String, Object>> getUserInfo(Authentication authentication) {
    User user = userService.getCurrentUser(authentication);

    // 获取用户角色和权限
    Set<String> roles = userService.getUserRoles(user.getId());
    Set<String> permissions = userService.getUserPermissions(user.getId());

    Map<String, Object> userInfo = new HashMap<>();
    userInfo.put("id", user.getId());
    userInfo.put("companyId", user.getCompanyId());
    userInfo.put("departmentId", user.getDepartmentId());
    userInfo.put("username", user.getUsername());
    userInfo.put("nickname", user.getNickname());
    userInfo.put("avatar", user.getAvatar());
    userInfo.put("email", user.getEmail());
    userInfo.put("phone", user.getPhone());
    userInfo.put("gender", user.getGender());
    userInfo.put("roles", roles);
    userInfo.put("permissions", permissions);

    return Result.success(userInfo);
  }

  /**
   * 分页查询用户列表
   *
   * @param pageNum  页码
   * @param pageSize 每页大小
   * @param keyword  关键字
   * @param deptId   部门ID
   * @param enabled  状态
   * @return 用户分页列表
   */
  @GetMapping
  @PreAuthorize("hasAuthority('system:user:list')")
  public Result<Page<UserDto>> listUsers(
      @RequestParam(defaultValue = "1") int pageNum,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long deptId,
      @RequestParam(required = false) Boolean enabled) {

    Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").descending());
    Page<User> users = userService.findUsers(keyword, deptId, enabled, pageable);

    return Result.success(users.map(this::convertToDto));
  }

  /**
   * 获取用户详情
   *
   * @param id 用户ID
   * @return 用户详情
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('system:user:query')")
  public Result<User> getUserDetail(@PathVariable Long id) {
    User user = userService.findById(id);
    return Result.success(user);
  }

  /**
   * 创建用户
   *
   * @param userDto 用户信息
   * @return 创建结果
   */
  @PostMapping
  @PreAuthorize("hasAuthority('system:user:add')")
  public Result<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
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
    user.setEnabled(userDto.getEnabled());

    User createdUser = userService.createUser(user);

    // 如果指定了角色，分配角色
    if (userDto.getRoleIds() != null && !userDto.getRoleIds().isEmpty()) {
      userService.assignRoles(createdUser.getId(), userDto.getRoleIds());
    }

    // 转换为DTO
    UserDto createdUserDto = convertToDto(createdUser);

    return Result.success("创建成功", createdUserDto);
  }

  /**
   * 更新用户
   *
   * @param id      用户ID
   * @param userDto 用户信息
   * @return 更新结果
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('system:user:edit')")
  public Result<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
    // 检查用户是否存在
    User existingUser = userService.findById(id);

    // 检查用户名是否已存在
    if (!existingUser.getUsername().equals(userDto.getUsername()) &&
        userService.isUsernameExists(userDto.getUsername())) {
      return Result.error(400, "用户名已存在");
    }

    // 检查邮箱是否已存在
    if (userDto.getEmail() != null &&
        !userDto.getEmail().equals(existingUser.getEmail()) &&
        userService.isEmailExists(userDto.getEmail())) {
      return Result.error(400, "邮箱已存在");
    }

    // 检查手机号是否已存在
    if (userDto.getPhone() != null &&
        !userDto.getPhone().equals(existingUser.getPhone()) &&
        userService.isPhoneExists(userDto.getPhone())) {
      return Result.error(400, "手机号已存在");
    }

    // 更新用户
    existingUser.setCompanyId(userDto.getCompanyId());
    existingUser.setDepartmentId(userDto.getDepartmentId());
    existingUser.setUsername(userDto.getUsername());
    existingUser.setNickname(userDto.getNickname());
    existingUser.setEmail(userDto.getEmail());
    existingUser.setPhone(userDto.getPhone());
    existingUser.setGender(userDto.getGender());
    existingUser.setEnabled(userDto.getEnabled());

    User updatedUser = userService.updateUser(existingUser);

    // 如果指定了角色，更新角色
    if (userDto.getRoleIds() != null) {
      userService.assignRoles(updatedUser.getId(), userDto.getRoleIds());
    }

    // 转换为DTO
    UserDto updatedUserDto = convertToDto(updatedUser);

    return Result.success("更新成功", updatedUserDto);
  }

  /**
   * 删除用户
   *
   * @param id 用户ID
   * @return 删除结果
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('system:user:delete')")
  public Result<String> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return Result.success("删除成功");
  }

  /**
   * 批量删除用户
   *
   * @param ids 用户ID列表
   * @return 删除结果
   */
  @DeleteMapping("/batch")
  @PreAuthorize("hasAuthority('system:user:delete')")
  public Result<String> batchDeleteUsers(@RequestBody List<Long> ids) {
    ids.forEach(userService::deleteUser);
    return Result.success("批量删除成功");
  }

  /**
   * 重置用户密码
   *
   * @param id 用户ID
   * @return 重置结果
   */
  @PutMapping("/{id}/password/reset")
  @PreAuthorize("hasAuthority('system:user:resetPwd')")
  public Result<String> resetPassword(@PathVariable Long id) {
    String newPassword = userService.resetPassword(id);
    return Result.success("重置密码成功", newPassword);
  }

  /**
   * 修改密码
   *
   * @param params 密码参数
   * @return 修改结果
   */
  @PutMapping("/password")
  public Result<String> updatePassword(
      @RequestBody Map<String, String> params,
      Authentication authentication) {

    User user = userService.getCurrentUser(authentication);
    String oldPassword = params.get("oldPassword");
    String newPassword = params.get("newPassword");

    userService.updatePassword(user.getId(), oldPassword, newPassword);
    return Result.success("修改密码成功");
  }

  /**
   * 更新用户状态
   *
   * @param id      用户ID
   * @param enabled 状态
   * @return 更新结果
   */
  @PutMapping("/status")
  @PreAuthorize("hasAuthority('system:user:edit')")
  public Result<String> updateStatus(@RequestParam Long id, @RequestParam Boolean enabled) {
    userService.updateEnabled(id, enabled);
    return Result.success("更新状态成功");
  }

  /**
   * 为用户分配角色
   *
   * @param id      用户ID
   * @param roleIds 角色ID列表
   * @return 分配结果
   */
  @PostMapping("/{id}/roles")
  @PreAuthorize("hasAuthority('system:user:edit')")
  public Result<String> assignRoles(@PathVariable Long id, @RequestBody Set<Long> roleIds) {
    userService.assignRoles(id, roleIds);
    return Result.success("分配角色成功");
  }

  /**
   * 将用户实体转换为DTO
   *
   * @param user 用户实体
   * @return 用户DTO
   */
  private UserDto convertToDto(User user) {
    UserDto dto = new UserDto();
    dto.setId(user.getId());
    dto.setCompanyId(user.getCompanyId());
    dto.setCompanyName(user.getCompany() != null ? user.getCompany().getName() : null);
    dto.setDepartmentId(user.getDepartmentId());
    dto.setDepartmentName(user.getDepartment() != null ? user.getDepartment().getName() : null);
    dto.setUsername(user.getUsername());
    dto.setNickname(user.getNickname());
    dto.setEmail(user.getEmail());
    dto.setPhone(user.getPhone());
    dto.setGender(user.getGender());
    dto.setEnabled(user.getEnabled());
    dto.setDepartmentId(user.getDepartment() != null ? user.getDepartment().getId() : null);
    dto.setRoleIds(user.getRoles().stream().map(role -> role.getId()).collect(Collectors.toSet()));
    return dto;
  }
}