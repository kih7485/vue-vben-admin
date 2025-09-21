package com.winus.express.modules.system.role.repository;

import com.winus.express.modules.system.role.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Role Repository
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    /**
     * Find role by role code
     */
    Optional<Role> findByRoleCodeAndDelFlag(String roleCode, String delFlag);

    /**
     * Find all active roles
     */
    List<Role> findByStatusAndDelFlagOrderBySortNo(String status, String delFlag);

    /**
     * Find roles by status with pagination
     */
    Page<Role> findByStatusAndDelFlag(String status, String delFlag, Pageable pageable);

    /**
     * Check if role code exists
     */
    boolean existsByRoleCodeAndDelFlag(String roleCode, String delFlag);

    /**
     * Search roles by keyword
     */
    @Query("SELECT r FROM Role r WHERE r.delFlag = :delFlag AND " +
           "(r.roleName LIKE %:keyword% OR r.roleCode LIKE %:keyword%)")
    Page<Role> searchRoles(@Param("keyword") String keyword, @Param("delFlag") String delFlag, Pageable pageable);

    /**
     * Find role by ID and delete flag
     */
    Optional<Role> findByRoleIdAndDelFlag(String roleId, String delFlag);

    /**
     * Find roles by delete flag
     */
    Page<Role> findByDelFlag(String delFlag, Pageable pageable);

    /**
     * Find roles by user ID
     */
    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.userId = :userId AND r.delFlag = '0' AND r.status = '1'")
    List<Role> findByUserId(@Param("userId") String userId);

    /**
     * Find roles with menu permissions
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN FETCH r.menus WHERE r.delFlag = '0' AND r.status = '1'")
    List<Role> findRolesWithMenus();

    /**
     * Count roles by status
     */
    long countByStatusAndDelFlag(String status, String delFlag);
}