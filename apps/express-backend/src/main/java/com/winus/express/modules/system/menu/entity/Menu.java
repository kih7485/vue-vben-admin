package com.winus.express.modules.system.menu.entity;

import com.winus.express.common.dto.BaseEntity;
import com.winus.express.modules.system.role.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Menu Entity - TB_MENU
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"roles", "children", "parent"})
@ToString(exclude = {"roles", "children", "parent"})
@Entity
@Table(name = "TB_MENU")
public class Menu extends BaseEntity {

    @Id
    @Column(name = "MENU_ID", length = 20)
    private String menuId;

    @Column(name = "PARENT_ID", length = 20)
    private String parentId = "0";

    @NotBlank
    @Size(max = 50)
    @Column(name = "MENU_NAME", nullable = false, length = 50)
    private String menuName;

    @Column(name = "MENU_PATH", length = 200)
    private String menuPath;

    @Column(name = "COMPONENT", length = 255)
    private String component;

    @NotBlank
    @Column(name = "MENU_TYPE", length = 1, nullable = false)
    private String menuType;

    @Column(name = "VISIBLE", length = 1, columnDefinition = "CHAR(1) DEFAULT '1'")
    private String visible = "1";

    @Column(name = "STATUS", length = 1, columnDefinition = "CHAR(1) DEFAULT '1'")
    private String status = "1";

    @Column(name = "PERMS", length = 100)
    private String perms;

    @Column(name = "ICON", length = 100)
    private String icon;

    @Column(name = "SORT_NO")
    private Integer sortNo = 0;

    @Column(name = "IS_FRAME", length = 1, columnDefinition = "CHAR(1) DEFAULT '0'")
    private String isFrame = "0";

    @Column(name = "IS_CACHE", length = 1, columnDefinition = "CHAR(1) DEFAULT '1'")
    private String isCache = "1";

    @Column(name = "DEL_FLAG", length = 1, columnDefinition = "CHAR(1) DEFAULT '0'")
    private String delFlag = "0";

    @ManyToMany(mappedBy = "menus", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Menu> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "MENU_ID", insertable = false, updatable = false)
    private Menu parent;

    // Constructor with essential fields
    public Menu(String menuId, String menuName, String menuType) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuType = menuType;
    }

    // Utility methods
    public boolean isVisible() {
        return "1".equals(visible);
    }

    public boolean isActive() {
        return "1".equals(status);
    }

    public boolean isDirectory() {
        return "M".equals(menuType);
    }

    public boolean isMenu() {
        return "C".equals(menuType);
    }

    public boolean isButton() {
        return "F".equals(menuType);
    }

    // Permission getter for compatibility
    public String getPermission() {
        return perms;
    }

    public void setPermission(String permission) {
        this.perms = permission;
    }
}