package com.xcesys.extras.feemgmt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户数据传输对象，用于前后端之间的用户数据交换
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3到20个字符之间")
    private String username;

    /**
     * 密码
     */
    @Size(min = 6, max = 100, message = "密码长度必须在6到100个字符之间")
    private String password;

    /**
     * 用户昵称
     */
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickname;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private Integer gender;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 帐号状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 角色ID列表
     */
    private Set<Long> roleIds = new HashSet<>();
}