package com.xcesys.template.admin.security;

import com.xcesys.template.admin.entity.Permission;
import com.xcesys.template.admin.entity.Role;
import com.xcesys.template.admin.entity.User;
import com.xcesys.template.admin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security用户详情服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 获取用户权限列表
        Set<String> permissions = getUserPermissions(user);
        
        // 构建并返回UserDetails对象
        return UserDetailsImpl.build(user, permissions);
    }

    /**
     * 获取用户的权限列表
     *
     * @param user 用户对象
     * @return 权限代码集合
     */
    private Set<String> getUserPermissions(User user) {
        Set<String> permissions = new HashSet<>();
        
        // 添加所有角色作为权限
        Set<String> rolePermissions = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getCode())
                .collect(Collectors.toSet());
        permissions.addAll(rolePermissions);
        
        // 添加角色关联的权限
        Set<String> menuPermissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet());
        permissions.addAll(menuPermissions);
        
        // 如果用户有管理员角色，添加特殊的管理员权限
        boolean isAdmin = user.getRoles().stream()
                .map(Role::getCode)
                .anyMatch("admin"::equalsIgnoreCase);
        if (isAdmin) {
            permissions.add("admin");
        }
        
        return permissions;
    }
}