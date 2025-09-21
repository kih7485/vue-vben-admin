package com.winus.express.modules.system.department.entity;

import com.winus.express.common.dto.BaseEntity;
import com.winus.express.modules.system.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Department Entity - TB_DEPT
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"children", "parent", "users"})
@ToString(exclude = {"children", "parent", "users"})
@Entity
@Table(name = "TB_DEPT")
public class Department extends BaseEntity {

    @Id
    @Column(name = "DEPT_ID", length = 50)
    private String deptId;

    @Column(name = "DEPT_CODE", length = 20, unique = true, nullable = false)
    private String deptCode;

    @Column(name = "PARENT_ID", length = 50)
    private String parentId = "0";

    @Column(name = "PARENT_CODE", length = 20)
    private String parentCode = "0";

    @Column(name = "ANCESTORS", length = 500)
    private String ancestors;

    @NotBlank
    @Size(max = 50)
    @Column(name = "DEPT_NAME", nullable = false, length = 50)
    private String deptName;

    @Column(name = "LEADER", length = 50)
    private String leader;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "SORT_NO")
    private Integer sortNo = 0;

    @Column(name = "STATUS", length = 1, columnDefinition = "CHAR(1) DEFAULT '1'")
    private String status = "1";

    @Column(name = "DEL_FLAG", length = 1, columnDefinition = "CHAR(1) DEFAULT '0'")
    private String delFlag = "0";

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Department> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CODE", referencedColumnName = "DEPT_CODE", insertable = false, updatable = false)
    private Department parent;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    // Constructor with essential fields
    public Department(String deptCode, String deptName) {
        this.deptCode = deptCode;
        this.deptName = deptName;
    }

    // Utility methods
    public boolean isActive() {
        return "1".equals(status) && "0".equals(delFlag);
    }

    public boolean isRootDepartment() {
        return "0".equals(parentCode);
    }
}