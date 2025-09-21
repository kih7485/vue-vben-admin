package com.winus.express.modules.system.menu.controller;

import com.winus.express.common.dto.ApiResponse;
import com.winus.express.modules.system.menu.dto.MenuDto;
import com.winus.express.modules.system.menu.entity.Menu;
import com.winus.express.modules.system.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENU_VIEW')")
    public ResponseEntity<List<Menu>> getAllMenus() {
        List<Menu> menus = menuService.getAllMenus();
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/tree")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENU_VIEW')")
    public ResponseEntity<List<Menu>> getMenuTree() {
        List<Menu> menuTree = menuService.getMenuTree();
        return ResponseEntity.ok(menuTree);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Menu>> getUserMenus(@PathVariable String userId) {
        List<Menu> userMenus = menuService.getUserMenus(userId);
        return ResponseEntity.ok(userMenus);
    }

    @GetMapping("/{menuId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENU_VIEW')")
    public ResponseEntity<Menu> getMenuById(@PathVariable String menuId) {
        Optional<Menu> menu = menuService.getMenuById(menuId);
        return menu.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENU_CREATE')")
    public ResponseEntity<ApiResponse<Menu>> createMenu(@Valid @RequestBody MenuDto menuDto) {
        try {
            Menu createdMenu = menuService.createMenu(menuDto);
            return ResponseEntity.ok(ApiResponse.success("메뉴가 성공적으로 생성되었습니다.", createdMenu));
        } catch (RuntimeException e) {
            log.error("Failed to create menu: {}", menuDto.getMenuName(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{menuId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENU_UPDATE')")
    public ResponseEntity<ApiResponse<Menu>> updateMenu(
            @PathVariable String menuId,
            @Valid @RequestBody MenuDto menuDto) {
        try {
            Menu updatedMenu = menuService.updateMenu(menuId, menuDto);
            return ResponseEntity.ok(ApiResponse.success("메뉴 정보가 성공적으로 업데이트되었습니다.", updatedMenu));
        } catch (RuntimeException e) {
            log.error("Failed to update menu: {}", menuId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{menuId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENU_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable String menuId) {
        try {
            menuService.deleteMenu(menuId);
            return ResponseEntity.ok(ApiResponse.success("메뉴가 성공적으로 삭제되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to delete menu: {}", menuId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENU_VIEW')")
    public ResponseEntity<List<Menu>> getMenusByParent(@PathVariable String parentId) {
        List<Menu> menus = menuService.getMenusByParentId(parentId);
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/type/{menuType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENU_VIEW')")
    public ResponseEntity<List<Menu>> getMenusByType(@PathVariable String menuType) {
        List<Menu> menus = menuService.getMenusByType(menuType);
        return ResponseEntity.ok(menus);
    }

    @PutMapping("/{menuId}/sort")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateMenuSort(
            @PathVariable String menuId,
            @RequestParam int sortNo) {
        try {
            menuService.updateMenuSort(menuId, sortNo);
            return ResponseEntity.ok(ApiResponse.success("메뉴 순서가 성공적으로 업데이트되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to update menu sort: {}", menuId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENU_VIEW')")
    public ResponseEntity<List<Menu>> getPermissionMenus() {
        List<Menu> permissions = menuService.getPermissionMenus();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/check/path")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENU_VIEW')")
    public ResponseEntity<Boolean> checkMenuPathExists(
            @RequestParam String menuPath,
            @RequestParam(required = false) String excludeMenuId) {
        boolean exists = menuService.existsByMenuPath(menuPath, excludeMenuId);
        return ResponseEntity.ok(exists);
    }
}