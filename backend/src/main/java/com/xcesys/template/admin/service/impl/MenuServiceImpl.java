package com.xcesys.template.admin.service.impl;

import com.xcesys.template.admin.entity.Menu;
import com.xcesys.template.admin.exception.BusinessException;
import com.xcesys.template.admin.repository.MenuRepository;
import com.xcesys.template.admin.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * 菜单服务实现类
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

  private final MenuRepository menuRepository;

  @Override
  public Menu findById(Long id) {
    return menuRepository.findById(id)
        .orElseThrow(() -> BusinessException.notFoundError("菜单不存在: ID=" + id));
  }

  @Override
  public List<Menu> findMenuTree() {
    // 获取所有菜单
    List<Menu> allMenus = menuRepository.findAll(Sort.by("sort").ascending());

    // 构建菜单树
    return buildMenuTree(allMenus, null);
  }

  @Override
  public List<Menu> findUserMenuTree(Long userId) {
    // 获取用户可访问的菜单
    List<Menu> userMenus = menuRepository.findByUserId(userId);

    // 构建菜单树
    return buildMenuTree(userMenus, null);
  }

  /**
   * 构建菜单树结构
   *
   * @param menus    所有菜单列表
   * @param parentId 父菜单ID
   * @return 菜单树结构
   */
  private List<Menu> buildMenuTree(List<Menu> menus, Long parentId) {
    List<Menu> tree = new ArrayList<>();

    for (Menu menu : menus) {
      // 如果是根菜单
      if ((parentId == null && menu.getParentId() == null) ||
          (parentId != null && parentId.equals(menu.getParentId()))) {
        // 递归获取子菜单
        List<Menu> children = buildMenuTree(menus, menu.getId());
        if (!children.isEmpty()) {
          menu.setChildren(children);
        }
        tree.add(menu);
      }
    }

    // 根据排序字段排序
    tree.sort(Comparator.comparing(Menu::getSort, Comparator.nullsLast(Comparator.naturalOrder())));

    return tree;
  }

  @Override
  @Transactional
  public Menu createMenu(Menu menu) {
    // 验证父菜单是否存在
    if (menu.getParentId() != null) {
      Menu parentMenu = findById(menu.getParentId());
      if (!Boolean.TRUE.equals(parentMenu.getEnabled())) {
        throw BusinessException.validationError("父菜单已停用，无法创建子菜单");
      }
    }

    // 设置默认值
    if (menu.getVisible() == null) {
      menu.setVisible(true);
    }

    if (menu.getEnabled() == null) {
      menu.setEnabled(true);
    }

    if (menu.getIsExternal() == null) {
      menu.setIsExternal(false);
    }

    if (menu.getIsCache() == null) {
      menu.setIsCache(false);
    }

    return menuRepository.save(menu);
  }

  @Override
  @Transactional
  public Menu updateMenu(Menu menu) {
    // 检查菜单是否存在
    Menu existingMenu = findById(menu.getId());

    // 检查是否将菜单的父节点设置为自己的子节点，防止产生循环引用
    if (menu.getParentId() != null) {
      List<Menu> children = findMenuWithChildren(menu.getId());
      for (Menu child : children) {
        if (child.getId().equals(menu.getParentId())) {
          throw BusinessException.validationError("不能将菜单的父节点设置为其子节点");
        }
      }

      // 验证父菜单是否存在
      Menu parentMenu = findById(menu.getParentId());
      if (!Boolean.TRUE.equals(parentMenu.getEnabled())) {
        throw BusinessException.validationError("父菜单已停用，无法设置为父菜单");
      }
    }

    return menuRepository.save(menu);
  }

  @Override
  @Transactional
  public void deleteMenu(Long id) {
    // 检查是否有子菜单
    if (hasChildMenu(id)) {
      throw BusinessException.validationError("存在子菜单，无法删除");
    }

    Menu menu = findById(id);
    menuRepository.delete(menu);
  }

  @Override
  public List<Menu> findMenuWithChildren(Long id) {
    // 查找当前菜单及其所有子菜单
    Menu menu = findById(id);
    List<Menu> result = new ArrayList<>();
    result.add(menu);

    // 递归查询子菜单
    findChildrenRecursively(id, result);

    return result;
  }

  /**
   * 递归查找子菜单
   *
   * @param parentId 父菜单ID
   * @param result   结果列表
   */
  private void findChildrenRecursively(Long parentId, List<Menu> result) {
    List<Menu> children = menuRepository.findByParentId(parentId, Sort.by("sort").ascending());
    if (!children.isEmpty()) {
      result.addAll(children);
      for (Menu child : children) {
        findChildrenRecursively(child.getId(), result);
      }
    }
  }

  @Override
  public Set<Menu> findMenusByRoleId(Long roleId) {
    return menuRepository.findByRoleId(roleId);
  }

  @Override
  public List<Menu> findMenusByUserId(Long userId) {
    return menuRepository.findByUserId(userId);
  }

  @Override
  public List<Menu> findByType(String type) {
    return menuRepository.findByType(type);
  }

  @Override
  public boolean hasChildMenu(Long id) {
    Sort sort = Sort.by("sort").ascending();
    List<Menu> children = menuRepository.findByParentId(id, sort);
    return !children.isEmpty();
  }

  @Override
  @Transactional
  public void updateEnabled(Long id, Boolean enabled) {
    Menu menu = findById(id);

    // 如果是启用菜单，需要检查父菜单是否已启用
    if (Boolean.TRUE.equals(enabled) && menu.getParentId() != null) {
      Menu parentMenu = findById(menu.getParentId());
      if (!Boolean.TRUE.equals(parentMenu.getEnabled())) {
        throw BusinessException.validationError("父菜单已停用，无法启用该菜单");
      }
    }

    // 如果是停用菜单，需要同时停用所有子菜单
    if (Boolean.TRUE.equals(enabled)) {
      updateChildrenStatus(id, enabled);
    }

    menu.setEnabled(enabled);
    menuRepository.save(menu);
  }

  /**
   * 递归更新子菜单状态
   *
   * @param parentId 父菜单ID
   * @param enabled   状态
   */
  private void updateChildrenStatus(Long parentId, Boolean enabled) {
    List<Menu> children = menuRepository.findByParentId(parentId, Sort.by("sort").ascending());
    for (Menu child : children) {
      child.setEnabled(enabled);
      menuRepository.save(child);
      updateChildrenStatus(child.getId(), enabled);
    }
  }
}