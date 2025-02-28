package com.xcesys.extras.feemgmt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 菜单实体类
 */
@Entity
@Table(name = "sys_menu")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE sys_menu SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Menu extends BaseEntity {

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * 父菜单ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 显示顺序
     */
    @Column(name = "sort")
    private Integer sort;

    /**
     * 路由地址
     */
    @Size(max = 200, message = "路由地址长度不能超过200个字符")
    @Column(name = "path")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 200, message = "组件路径长度不能超过200个字符")
    @Column(name = "component")
    private String component;

    /**
     * 路由参数
     */
    @Column(name = "query")
    private String query;

    /**
     * 是否为外链（0否 1是）
     */
    @Column(name = "is_external")
    private Boolean isExternal = false;

    /**
     * 是否缓存（0不缓存 1缓存）
     */
    @Column(name = "is_cache")
    private Boolean isCache = false;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Column(name = "type", nullable = false)
    private String type;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    @Column(name = "visible", nullable = false)
    private Boolean visible = true;

    /**
     * 菜单状态（0正常 1停用）
     */
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    /**
     * 权限标识
     */
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    @Column(name = "permission_code")
    private String permissionCode;

    /**
     * 菜单图标
     */
    @Size(max = 100, message = "菜单图标长度不能超过100个字符")
    @Column(name = "icon")
    private String icon;

    /**
     * 父菜单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Menu parent;

    /**
     * 子菜单
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("sort asc")
    private List<Menu> children = new ArrayList<>();

    /**
     * 角色和菜单多对多关系（由Role维护）
     */
    @ManyToMany(mappedBy = "menus")
    private Set<Role> roles = new HashSet<>();
}