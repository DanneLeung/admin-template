package com.xcesys.template.admin.mapper;

import com.xcesys.template.admin.dto.PermissionDto;
import com.xcesys.template.admin.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper extends BaseMapper<Permission, PermissionDto> {

}