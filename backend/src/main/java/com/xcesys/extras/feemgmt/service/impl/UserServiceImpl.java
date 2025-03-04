package com.xcesys.extras.feemgmt.service.impl;

import com.xcesys.extras.feemgmt.entity.Permission;
import com.xcesys.extras.feemgmt.entity.Role;
import com.xcesys.extras.feemgmt.entity.User;
import com.xcesys.extras.feemgmt.exception.BusinessException;
import com.xcesys.extras.feemgmt.repository.PermissionRepository;
import com.xcesys.extras.feemgmt.repository.RoleRepository;
import com.xcesys.extras.feemgmt.repository.UserRepository;
import com.xcesys.extras.feemgmt.security.UserDetailsImpl;
import com.xcesys.extras.feemgmt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFoundError("用户不存在: " + username));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFoundError("用户不存在: ID=" + id));
    }

    @Override
    @Transactional
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (isUsernameExists(user.getUsername())) {
            throw BusinessException.conflictError("用户名已存在: " + user.getUsername());
        }

        // 检查邮箱是否已存在
        if (StringUtils.hasText(user.getEmail()) && isEmailExists(user.getEmail())) {
            throw BusinessException.conflictError("邮箱已存在: " + user.getEmail());
        }

        // 检查手机号是否已存在
        if (StringUtils.hasText(user.getPhone()) && isPhoneExists(user.getPhone())) {
            throw BusinessException.conflictError("手机号已存在: " + user.getPhone());
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认头像
        if (!StringUtils.hasText(user.getAvatar())) {
            user.setAvatar("/avatar/default.jpg");
        }

        // 设置默认状态为启用
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = findById(user.getId());

        // 检查用户名是否已存在
        if (!existingUser.getUsername().equals(user.getUsername()) && isUsernameExists(user.getUsername())) {
            throw BusinessException.conflictError("用户名已存在: " + user.getUsername());
        }

        // 检查邮箱是否已存在
        if (StringUtils.hasText(user.getEmail()) && 
            !user.getEmail().equals(existingUser.getEmail()) && 
            isEmailExists(user.getEmail())) {
            throw BusinessException.conflictError("邮箱已存在: " + user.getEmail());
        }

        // 检查手机号是否已存在
        if (StringUtils.hasText(user.getPhone()) && 
            !user.getPhone().equals(existingUser.getPhone()) && 
            isPhoneExists(user.getPhone())) {
            throw BusinessException.conflictError("手机号已存在: " + user.getPhone());
        }

        // 不更新密码，密码应该通过专门的方法更新
        user.setPassword(existingUser.getPassword());
        
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Override
    public Page<User> findUsers(String keyword, Long deptId, Boolean enabled, Pageable pageable) {
        if (StringUtils.hasText(keyword)) {
            return userRepository.findByKeyword(keyword, pageable);
        } else if (deptId != null && enabled != null) {
            return userRepository.findByDepartmentIdAndEnabled(deptId, enabled, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    @Override
    public List<User> findByDeptId(Long deptId) {
        return userRepository.findByDepartmentId(deptId);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = findById(userId);
        
        // 校验旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw BusinessException.validationError("旧密码不正确");
        }
        
        // 加密并更新新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public String resetPassword(Long userId) {
        User user = findById(userId);
        
        // 生成随机密码：8位字符，包含字母、数字
        String newPassword = generateRandomPassword(8);
        
        // 加密并更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return newPassword;
    }

    @Override
    @Transactional
    public void updateEnabled(Long userId, Boolean enabled) {
        User user = findById(userId);
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, Set<Long> roleIds) {
        User user = findById(userId);
        
        // 清空原有角色
        user.getRoles().clear();
        
        // 添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            roleIds.forEach(roleId -> {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> BusinessException.notFoundError("角色不存在: ID=" + roleId));
                user.getRoles().add(role);
            });
        }
        
        userRepository.save(user);
    }

    @Override
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw BusinessException.unauthorizedError("用户未登录");
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return findById(userDetails.getId());
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneExists(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        Set<Permission> permissions = permissionRepository.findByUserId(userId);
        return permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getUserRoles(Long userId) {
        Set<Role> roles = roleRepository.findByUserId(userId);
        return roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
    }

    /**
     * 生成随机密码
     *
     * @param length 密码长度
     * @return 随机密码
     */
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }
}