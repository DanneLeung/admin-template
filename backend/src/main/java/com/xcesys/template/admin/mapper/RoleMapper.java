package com.xcesys.template.admin.mapper;

import com.xcesys.template.admin.dto.RoleDto;
import com.xcesys.template.admin.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper extends BaseMapper<Role, RoleDto> {

}