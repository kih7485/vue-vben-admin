package com.winus.express.modules.system.menu.repository;

import com.winus.express.modules.system.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Menu Repository
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {

    /**
     * Find all menus by status ordered by sort number
     */
    List<Menu> findByStatusOrderBySortNo(String status);

    /**
     * Find menus by parent ID with delete flag
     */
    List<Menu> findByParentIdAndDelFlagOrderBySortNo(String parentId, String delFlag);

    /**
     * Find menus by parent ID and status
     */
    List<Menu> findByParentIdAndStatusOrderBySortNo(String parentId, String status);

    /**
     * Find menus by menu type
     */
    List<Menu> findByMenuTypeAndStatusOrderBySortNo(String menuType, String status);

    /**
     * Find visible menus
     */
    List<Menu> findByVisibleAndStatusOrderBySortNo(String visible, String status);

    /**
     * Find menus by user roles
     */
    @Query("SELECT DISTINCT m FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.users u " +
           "WHERE u.userId = :userId " +
           "AND m.status = '1' " +
           "AND m.visible = '1' " +
           "AND r.status = '1' " +
           "AND r.delFlag = '0' " +
           "ORDER BY m.sortNo")
    List<Menu> findMenusByUserId(@Param("userId") String userId);

    /**
     * Find menu tree structure
     */
    @Query("SELECT m FROM Menu m WHERE m.status = '1' AND m.visible = '1' ORDER BY m.sortNo")
    List<Menu> findMenuTree();

    /**
     * Find root menus (parent_id = '0')
     */
    List<Menu> findByParentIdAndStatusAndVisibleOrderBySortNo(String parentId, String status, String visible);

    /**
     * Check if menu path exists
     */
    boolean existsByMenuPathAndMenuIdNot(String menuPath, String menuId);

    /**
     * Search menus by keyword
     */
    @Query("SELECT m FROM Menu m WHERE m.status = '1' AND " +
           "(m.menuName LIKE %:keyword% OR m.menuPath LIKE %:keyword% OR m.perms LIKE %:keyword%)")
    List<Menu> searchMenus(@Param("keyword") String keyword);

    /**
     * Find permissions by user ID
     */
    @Query("SELECT DISTINCT m.perms FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.users u " +
           "WHERE u.userId = :userId " +
           "AND m.perms IS NOT NULL " +
           "AND m.perms != '' " +
           "AND m.status = '1' " +
           "AND r.status = '1' " +
           "AND r.delFlag = '0'")
    List<String> findPermissionsByUserId(@Param("userId") String userId);

    /**
     * Count menus by parent ID
     */
    long countByParentIdAndStatus(String parentId, String status);

    /**
     * Find menu by ID and delete flag
     */
    Optional<Menu> findByMenuIdAndDelFlag(String menuId, String delFlag);

    /**
     * Find menus by status and delete flag
     */
    List<Menu> findByStatusAndDelFlagOrderBySortNo(String status, String delFlag);

    /**
     * Find menus by menu type, status and delete flag
     */
    List<Menu> findByMenuTypeAndStatusAndDelFlagOrderBySortNo(String menuType, String status, String delFlag);

    /**
     * Find menus by menu path and delete flag
     */
    List<Menu> findByMenuPathAndDelFlag(String menuPath, String delFlag);
}