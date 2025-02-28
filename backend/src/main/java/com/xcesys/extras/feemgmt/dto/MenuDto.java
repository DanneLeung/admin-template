package com.xcesys.extras.feemgmt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单数据传输对象，用于前后端之间的菜单数据交换
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50个字符")
    private String name;

    /**
     * 路由地址
     */
    @Size(max = 200, message = "路由地址长度不能超过200个字符")
    private String path;

    /**
     * 组件路径
     */
    @Size(max = 200, message = "组件路径长度不能超过200个字符")
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 是否为外链
     */
    private Boolean isExternal = false;

    /**
     * 是否缓存
     */
    private Boolean isCache = false;

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @NotBlank(message = "菜单类型不能为空")
    private String type;

    /**
     * 菜单状态（true显示 false隐藏）
     */
    private Boolean visible = true;

    /**
     * 菜单状态（0正常 1停用）
     */
    private Integer status = 0;

    /**
     * 权限标识
     */
    @Size(max = 100, message = "权限标识长度不能超过100个字符")
    private String permissionCode;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;
    
    /**
     * 备注
     */
    private String remark;
}