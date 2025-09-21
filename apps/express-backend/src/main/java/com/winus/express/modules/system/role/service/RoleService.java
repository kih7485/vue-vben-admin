package com.winus.express.modules.system.role.service;

import com.winus.express.modules.system.role.dto.RoleDto;
import com.winus.express.modules.system.role.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Role Service Interface
 */
public interface RoleService {

    /**
     * Create a new role
     */
    Role createRole(RoleDto roleDto);

    /**
     * Update an existing role
     */
    Role updateRole(String roleId, RoleDto roleDto);

    /**
     * Delete a role (soft delete)
     */
    void deleteRole(String roleId);

    /**
     * Get role by ID
     */
    Optional<Role> getRoleById(String roleId);

    /**
     * Get role by code
     */
    Optional<Role> getRoleByCode(String roleCode);

    /**
     * Get all active roles
     */
    List<Role> getAllActiveRoles();

    /**
     * Get roles with pagination
     */
    Page<Role> getRoles(Pageable pageable);

    /**
     * Search roles
     */
    Page<Role> searchRoles(String keyword, Pageable pageable);

    /**
     * Check if role code exists
     */
    boolean existsByRoleCode(String roleCode);

    /**
     * Update role menus
     */
    void updateRoleMenus(String roleId, List<String> menuIds);

    /**
     * Get role menu IDs
     */
    List<String> getRoleMenuIds(String roleId);

    /**
     * Get user roles
     */
    List<Role> getUserRoles(String userId);

    /**
     * Assign roles to user
     */
    void assignRolesToUser(String userId, List<String> roleIds);

    /**
     * Remove role from user
     */
    void removeRoleFromUser(String userId, String roleId);

    /**
     * Get role permissions
     */
    List<String> getRolePermissions(String roleId);
}