package com.xcesys.template.admin.mapper;

import com.xcesys.template.admin.dto.MenuDto;
import com.xcesys.template.admin.entity.Menu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MenuMapper extends BaseMapper<Menu, MenuDto> {
    /**
     * 将菜单实体转换为DTO
     *
     * @param menu 菜单实体
     * @return 菜单DTO
     */
    MenuDto toDto(Menu menu);

    /**
     * 将菜单DTO转换为实体
     *
     * @param menuDto 菜单DTO
     * @return 菜单实体
     */
    @Mapping(target = "id", ignore = true)
    void toEntity(@MappingTarget Menu menu, MenuDto menuDto);
}