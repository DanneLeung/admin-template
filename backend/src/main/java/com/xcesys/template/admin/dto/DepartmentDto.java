package com.xcesys.template.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepartmentDto {
    private Long id;

    @NotBlank(message = "部门名称不能为空")
    private String name;

    private Long parentId;

    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    private String leader;
    private String phone;
    private String email;
    private Boolean enabled;
    private String remark;
}