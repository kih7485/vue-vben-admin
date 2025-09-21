package com.winus.express.modules.system.user.repository;

import com.winus.express.modules.system.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Find user by username
     */
    Optional<User> findByUserNameAndDelFlag(String userName, String delFlag);

    /**
     * Find user by email
     */
    Optional<User> findByEmailAndDelFlag(String email, String delFlag);

    /**
     * Find users by department
     */
    List<User> findByDeptCodeAndDelFlagOrderBySortNo(String deptCode, String delFlag);

    /**
     * Find users by status
     */
    Page<User> findByStatusAndDelFlag(String status, String delFlag, Pageable pageable);

    /**
     * Find users by department and status
     */
    Page<User> findByDeptCodeAndStatusAndDelFlag(String deptCode, String status, String delFlag, Pageable pageable);

    /**
     * Check if username exists
     */
    boolean existsByUserNameAndDelFlag(String userName, String delFlag);

    /**
     * Check if email exists
     */
    boolean existsByEmailAndDelFlag(String email, String delFlag);

    /**
     * Search users by keyword
     */
    @Query("SELECT u FROM User u WHERE u.delFlag = :delFlag AND " +
           "(u.userName LIKE %:keyword% OR u.realName LIKE %:keyword% OR u.email LIKE %:keyword%)")
    Page<User> searchUsers(@Param("keyword") String keyword, @Param("delFlag") String delFlag, Pageable pageable);

    /**
     * Update user login info
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginTime = :loginTime, u.lastLoginIp = :loginIp WHERE u.userId = :userId")
    void updateLoginInfo(@Param("userId") String userId,
                        @Param("loginTime") LocalDateTime loginTime,
                        @Param("loginIp") String loginIp);

    /**
     * Update user password
     */
    @Modifying
    @Query("UPDATE User u SET u.password = :password, u.pwdResetTime = :resetTime WHERE u.userId = :userId")
    void updatePassword(@Param("userId") String userId,
                       @Param("password") String password,
                       @Param("resetTime") LocalDateTime resetTime);

    /**
     * Lock/Unlock user account
     */
    @Modifying
    @Query("UPDATE User u SET u.lockFlag = :lockFlag WHERE u.userId = :userId")
    void updateLockStatus(@Param("userId") String userId, @Param("lockFlag") String lockFlag);

    /**
     * Count users by department
     */
    long countByDeptCodeAndDelFlag(String deptCode, String delFlag);

    /**
     * Count active users
     */
    long countByStatusAndDelFlag(String status, String delFlag);
}