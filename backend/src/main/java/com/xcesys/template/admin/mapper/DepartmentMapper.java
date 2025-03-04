package com.xcesys.template.admin.mapper;

import com.xcesys.template.admin.dto.DepartmentDto;
import com.xcesys.template.admin.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper  extends BaseMapper<Department, DepartmentDto> {

}