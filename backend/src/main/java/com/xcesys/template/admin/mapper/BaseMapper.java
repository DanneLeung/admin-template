package com.xcesys.template.admin.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

public interface BaseMapper<T, D> {
  /**
   * 将实体转换为DTO
   *
   * @param t 实体
   * @return dto
   */
  @InheritConfiguration
  D toDto(T t);

  /**
   * 将DTO转换为实体
   *
   * @param t 实体
   * @param d DTO
   */
  @InheritConfiguration
  @Mapping(target = "id", ignore = true)
  void toEntity(@MappingTarget T t, D d);
}
