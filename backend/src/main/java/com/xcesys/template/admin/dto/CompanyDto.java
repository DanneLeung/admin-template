package com.xcesys.template.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyDto {
    private Long id;

    @NotBlank(message = "公司编码不能为空")
    private String code;

    @NotBlank(message = "公司名称不能为空")
    private String name;

    private String domain;
    private String description;
    private Boolean enabled;
}