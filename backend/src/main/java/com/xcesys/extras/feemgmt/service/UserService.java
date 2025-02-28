package com.xcesys.extras.feemgmt.service;

import com.xcesys.extras.feemgmt.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);

    /**
     * 获取用户详细信息
     *
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(Long id);

    /**
     * 创建用户
     *
     * @param user 用户对象
     * @return 创建后的用户对象
     */
    User createUser(User user);

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User updateUser(User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);

    /**
     * 分页查询用户列表
     *
     * @param keyword  关键字
     * @param deptId   部门ID
     * @param status   状态
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findUsers(String keyword, Long deptId, Integer status, Pageable pageable);

    /**
     * 根据部门ID获取用户列表
     *
     * @param deptId 部门ID
     * @return 用户列表
     */
    List<User> findByDeptId(Long deptId);

    /**
     * 修改用户密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @return 新密码
     */
    String resetPassword(Long userId);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     */
    void updateStatus(Long userId, Integer status);

    /**
     * 为用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, Set<Long> roleIds);

    /**
     * 获取当前登录用户
     *
     * @param authentication 认证信息
     * @return 当前登录用户
     */
    User getCurrentUser(Authentication authentication);

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 判断邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean isEmailExists(String email);

    /**
     * 判断手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean isPhoneExists(String phone);

    /**
     * 获取用户的权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 获取用户的角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    Set<String> getUserRoles(Long userId);
}