package com.xcesys.template.admin.mapper;

import com.xcesys.template.admin.dto.UserDto;
import com.xcesys.template.admin.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDto> {
  /**
   * 将用户实体转换为DTO
   *
   * @param user 用户实体
   * @return 用户DTO
   */
  @Mappings({
      @Mapping(target = "companyName", source = "company.name"),
      @Mapping(target = "departmentName", source = "department.name"),
  })
  UserDto toDto(User user);


}
