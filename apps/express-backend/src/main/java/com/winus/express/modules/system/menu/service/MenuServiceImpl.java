package com.winus.express.modules.system.menu.service;

import com.winus.express.modules.system.menu.dto.MenuDto;
import com.winus.express.modules.system.menu.entity.Menu;
import com.winus.express.modules.system.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Menu Service Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    @Override
    @Transactional
    public Menu createMenu(MenuDto menuDto) {
        Menu menu = new Menu();
        menu.setMenuId(UUID.randomUUID().toString());
        menu.setMenuName(menuDto.getMenuName());
        menu.setParentId(menuDto.getParentId() != null ? menuDto.getParentId() : "0");
        menu.setMenuPath(menuDto.getMenuPath());
        menu.setComponent(menuDto.getComponent());
        menu.setMenuType(menuDto.getMenuType());
        menu.setPermission(menuDto.getPermission());
        menu.setIcon(menuDto.getIcon());
        menu.setSortNo(menuDto.getSortNo() != null ? menuDto.getSortNo() : 0);
        menu.setVisible(menuDto.getVisible() != null ? menuDto.getVisible() : "1");
        menu.setStatus(menuDto.getStatus() != null ? menuDto.getStatus() : "1");
        menu.setRemark(menuDto.getRemark());
        menu.setCreateBy("system"); // TODO: Get from security context
        menu.setDelFlag("0");

        return menuRepository.save(menu);
    }

    @Override
    @Transactional
    public Menu updateMenu(String menuId, MenuDto menuDto) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + menuId));

        if (menuDto.getMenuName() != null) {
            menu.setMenuName(menuDto.getMenuName());
        }
        if (menuDto.getParentId() != null) {
            menu.setParentId(menuDto.getParentId());
        }
        if (menuDto.getMenuPath() != null) {
            menu.setMenuPath(menuDto.getMenuPath());
        }
        if (menuDto.getComponent() != null) {
            menu.setComponent(menuDto.getComponent());
        }
        if (menuDto.getMenuType() != null) {
            menu.setMenuType(menuDto.getMenuType());
        }
        if (menuDto.getPermission() != null) {
            menu.setPermission(menuDto.getPermission());
        }
        if (menuDto.getIcon() != null) {
            menu.setIcon(menuDto.getIcon());
        }
        if (menuDto.getSortNo() != null) {
            menu.setSortNo(menuDto.getSortNo());
        }
        if (menuDto.getVisible() != null) {
            menu.setVisible(menuDto.getVisible());
        }
        if (menuDto.getStatus() != null) {
            menu.setStatus(menuDto.getStatus());
        }
        if (menuDto.getRemark() != null) {
            menu.setRemark(menuDto.getRemark());
        }

        menu.setUpdateBy("system"); // TODO: Get from security context
        menu.setUpdateTime(LocalDateTime.now());

        return menuRepository.save(menu);
    }

    @Override
    @Transactional
    public void deleteMenu(String menuId) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + menuId));

        // Check if menu has children
        List<Menu> children = menuRepository.findByParentIdAndDelFlagOrderBySortNo(menuId, "0");
        if (!children.isEmpty()) {
            throw new RuntimeException("하위 메뉴가 있는 메뉴는 삭제할 수 없습니다");
        }

        menu.setDelFlag("1");
        menu.setUpdateBy("system"); // TODO: Get from security context
        menu.setUpdateTime(LocalDateTime.now());

        menuRepository.save(menu);
    }

    @Override
    public Optional<Menu> getMenuById(String menuId) {
        return menuRepository.findByMenuIdAndDelFlag(menuId, "0");
    }

    @Override
    public List<Menu> getAllMenus() {
        return menuRepository.findByStatusAndDelFlagOrderBySortNo("1", "0");
    }

    @Override
    public List<Menu> getMenuTree() {
        List<Menu> allMenus = getAllMenus();
        return buildMenuTree(allMenus);
    }

    @Override
    public List<Menu> getMenusByParentId(String parentId) {
        return menuRepository.findByParentIdAndDelFlagOrderBySortNo(
            parentId != null ? parentId : "0", "0");
    }

    @Override
    public List<Menu> getMenusByType(String menuType) {
        return menuRepository.findByMenuTypeAndStatusAndDelFlagOrderBySortNo(menuType, "1", "0");
    }

    @Override
    public List<Menu> getUserMenus(String userId) {
        List<Menu> userMenus = menuRepository.findMenusByUserId(userId);
        return buildMenuTree(userMenus);
    }

    @Override
    public List<Menu> getPermissionMenus() {
        return menuRepository.findByMenuTypeAndStatusAndDelFlagOrderBySortNo("B", "1", "0");
    }

    @Override
    @Transactional
    public void updateMenuSort(String menuId, int sortNo) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new RuntimeException("메뉴를 찾을 수 없습니다: " + menuId));

        menu.setSortNo(sortNo);
        menu.setUpdateTime(LocalDateTime.now());
        menuRepository.save(menu);
    }

    @Override
    public boolean existsByMenuPath(String menuPath, String excludeMenuId) {
        List<Menu> menus = menuRepository.findByMenuPathAndDelFlag(menuPath, "0");

        if (excludeMenuId != null) {
            menus = menus.stream()
                .filter(menu -> !menu.getMenuId().equals(excludeMenuId))
                .collect(Collectors.toList());
        }

        return !menus.isEmpty();
    }

    @Override
    public List<Menu> buildMenuTree(List<Menu> menus) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }

        // Group menus by parent ID
        Map<String, List<Menu>> menuMap = menus.stream()
            .collect(Collectors.groupingBy(
                menu -> menu.getParentId() != null ? menu.getParentId() : "0"
            ));

        // Build tree structure
        List<Menu> rootMenus = menuMap.getOrDefault("0", new ArrayList<>());

        for (Menu rootMenu : rootMenus) {
            buildChildren(rootMenu, menuMap);
        }

        // Sort by sortNo
        rootMenus.sort(Comparator.comparing(Menu::getSortNo));

        return rootMenus;
    }

    private void buildChildren(Menu parent, Map<String, List<Menu>> menuMap) {
        List<Menu> children = menuMap.get(parent.getMenuId());

        if (children != null && !children.isEmpty()) {
            children.sort(Comparator.comparing(Menu::getSortNo));
            parent.setChildren(children);

            for (Menu child : children) {
                buildChildren(child, menuMap);
            }
        }
    }
}