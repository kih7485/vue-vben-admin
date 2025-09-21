package com.winus.express.modules.system.user.entity;

import com.winus.express.common.dto.BaseEntity;
import com.winus.express.modules.system.role.entity.Role;
import com.winus.express.modules.system.department.entity.Department;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity - TB_USER
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"roles", "department"})
@ToString(exclude = {"password", "roles", "department"})
@Entity
@Table(name = "TB_USER")
public class User extends BaseEntity {

    @Id
    @Column(name = "USER_ID", length = 50)
    private String userId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "USER_NAME", nullable = false, length = 100)
    private String userName;

    @NotBlank
    @Size(max = 255)
    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Size(max = 50)
    @Column(name = "REAL_NAME", length = 50)
    private String realName;

    @Email
    @Size(max = 100)
    @Column(name = "EMAIL", length = 100, unique = true)
    private String email;

    @Size(max = 20)
    @Column(name = "PHONE", length = 20)
    private String phone;

    @Size(max = 20)
    @Column(name = "DEPT_CODE", length = 20)
    private String deptCode;

    @Column(name = "STATUS", length = 1, columnDefinition = "CHAR(1) DEFAULT '1'")
    private String status = "1";

    @Column(name = "AVATAR", length = 500)
    private String avatar;

    @Column(name = "LAST_LOGIN_TIME")
    private LocalDateTime lastLoginTime;

    @Column(name = "LAST_LOGIN_IP", length = 50)
    private String lastLoginIp;

    @Column(name = "PWD_RESET_TIME")
    private LocalDateTime pwdResetTime;

    @Column(name = "LOCK_FLAG", length = 1, columnDefinition = "CHAR(1) DEFAULT '0'")
    private String lockFlag = "0";

    @Column(name = "DEL_FLAG", length = 1, columnDefinition = "CHAR(1) DEFAULT '0'")
    private String delFlag = "0";

    @Column(name = "SORT_NO")
    private Integer sortNo = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "TB_USER_ROLE",
        joinColumns = @JoinColumn(name = "USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_CODE", referencedColumnName = "DEPT_CODE", insertable = false, updatable = false)
    private Department department;

    // Constructor with essential fields
    public User(String userId, String userName, String password) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
    }

    // Utility methods
    public boolean isEnabled() {
        return "1".equals(status) && "0".equals(delFlag) && "0".equals(lockFlag);
    }

    public boolean isAccountNonLocked() {
        return "0".equals(lockFlag);
    }

    public boolean isActive() {
        return "1".equals(status) && "0".equals(delFlag);
    }
}