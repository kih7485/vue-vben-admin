package com.winus.express.modules.system.role.entity;

import com.winus.express.common.dto.BaseEntity;
import com.winus.express.modules.system.user.entity.User;
import com.winus.express.modules.system.menu.entity.Menu;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Role Entity - TB_ROLE
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"users", "menus"})
@ToString(exclude = {"users", "menus"})
@Entity
@Table(name = "TB_ROLE")
public class Role extends BaseEntity {

    @Id
    @Column(name = "ROLE_ID", length = 20)
    private String roleId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "ROLE_NAME", nullable = false, length = 50)
    private String roleName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "ROLE_CODE", nullable = false, length = 50, unique = true)
    private String roleCode;

    @Size(max = 200)
    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "SORT_NO")
    private Integer sortNo = 0;

    @Column(name = "STATUS", length = 1, columnDefinition = "CHAR(1) DEFAULT '1'")
    private String status = "1";

    @Column(name = "DATA_SCOPE", length = 1, columnDefinition = "CHAR(1) DEFAULT '1'")
    private String dataScope = "1";

    @Column(name = "DEL_FLAG", length = 1, columnDefinition = "CHAR(1) DEFAULT '0'")
    private String delFlag = "0";

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "TB_ROLE_MENU",
        joinColumns = @JoinColumn(name = "ROLE_ID"),
        inverseJoinColumns = @JoinColumn(name = "MENU_ID")
    )
    private Set<Menu> menus = new HashSet<>();

    // Constructor with essential fields
    public Role(String roleId, String roleName, String roleCode) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleCode = roleCode;
    }

    // Utility methods
    public boolean isActive() {
        return "1".equals(status) && "0".equals(delFlag);
    }
}