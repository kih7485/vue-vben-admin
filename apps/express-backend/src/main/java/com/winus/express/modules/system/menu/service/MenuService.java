package com.winus.express.modules.system.menu.service;

import com.winus.express.modules.system.menu.dto.MenuDto;
import com.winus.express.modules.system.menu.entity.Menu;

import java.util.List;
import java.util.Optional;

/**
 * Menu Service Interface
 */
public interface MenuService {

    /**
     * Create a new menu
     */
    Menu createMenu(MenuDto menuDto);

    /**
     * Update an existing menu
     */
    Menu updateMenu(String menuId, MenuDto menuDto);

    /**
     * Delete a menu (soft delete)
     */
    void deleteMenu(String menuId);

    /**
     * Get menu by ID
     */
    Optional<Menu> getMenuById(String menuId);

    /**
     * Get all menus
     */
    List<Menu> getAllMenus();

    /**
     * Get menu tree structure
     */
    List<Menu> getMenuTree();

    /**
     * Get menus by parent ID
     */
    List<Menu> getMenusByParentId(String parentId);

    /**
     * Get menus by type
     */
    List<Menu> getMenusByType(String menuType);

    /**
     * Get user menus (based on roles)
     */
    List<Menu> getUserMenus(String userId);

    /**
     * Get permission menus
     */
    List<Menu> getPermissionMenus();

    /**
     * Update menu sort order
     */
    void updateMenuSort(String menuId, int sortNo);

    /**
     * Check if menu path exists
     */
    boolean existsByMenuPath(String menuPath, String excludeMenuId);

    /**
     * Build menu tree from flat list
     */
    List<Menu> buildMenuTree(List<Menu> menus);
}