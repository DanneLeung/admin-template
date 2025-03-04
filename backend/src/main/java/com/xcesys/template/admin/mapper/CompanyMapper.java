package com.xcesys.template.admin.mapper;

import com.xcesys.template.admin.dto.CompanyDto;
import com.xcesys.template.admin.entity.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper extends BaseMapper<Company, CompanyDto> {

}