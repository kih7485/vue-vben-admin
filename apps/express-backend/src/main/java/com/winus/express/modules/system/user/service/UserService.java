package com.winus.express.modules.system.user.service;

import com.winus.express.modules.system.user.dto.UserDto;
import com.winus.express.modules.system.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * User Service Interface
 */
public interface UserService {

    /**
     * Create user
     */
    User createUser(UserDto userDto);

    /**
     * Update user
     */
    User updateUser(String userId, UserDto userDto);

    /**
     * Delete user
     */
    void deleteUser(String userId);

    /**
     * Get user by ID
     */
    Optional<User> getUserById(String userId);

    /**
     * Get user by username
     */
    Optional<User> getUserByUsername(String username);

    /**
     * Get user by email
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Get users with pagination
     */
    Page<User> getUsers(Pageable pageable);

    /**
     * Search users
     */
    Page<User> searchUsers(String keyword, Pageable pageable);

    /**
     * Get users by department
     */
    List<User> getUsersByDepartment(String deptCode);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Update user password
     */
    void updatePassword(String userId, String oldPassword, String newPassword);

    /**
     * Reset user password
     */
    void resetPassword(String userId, String newPassword);

    /**
     * Lock user account
     */
    void lockUser(String userId);

    /**
     * Unlock user account
     */
    void unlockUser(String userId);

    /**
     * Update user roles
     */
    void updateUserRoles(String userId, List<String> roleIds);

    /**
     * Get user permissions
     */
    List<String> getUserPermissions(String userId);

    /**
     * Validate user credentials
     */
    boolean validateCredentials(String username, String password);

    /**
     * Update login info
     */
    void updateLoginInfo(String userId, String loginIp);
}