package com.xcesys.template.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类
 */
@Entity
@Table(name = "sys_role")
@Getter
@Setter
@NoArgsConstructor
// 移除以下软删除注解
// @SQLDelete(sql = "UPDATE sys_role SET deleted = true WHERE id = ? and version = ?")
// @Where(clause = "deleted = false")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 角色权限字符串
     */
    @NotBlank(message = "角色权限字符串不能为空")
    @Size(max = 100, message = "角色权限字符串长度不能超过100个字符")
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    /**
     * 显示顺序
     */
    @Column(name = "sort")
    private Integer sort;
    
    /**
     * 角色状态（true 启用 false 禁用）
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限 5：仅本人数据权限）
     */
    @Column(name = "data_scope")
    private Integer dataScope = 1;
    
    /**
     * 角色和权限多对多关系
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sys_role_permission",
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    private Set<Permission> permissions = new HashSet<>();

    /**
     * 角色和菜单多对多关系
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "sys_role_menu",
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "menu_id", referencedColumnName = "id")
    )
    private Set<Menu> menus = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    // getter 和 setter
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}