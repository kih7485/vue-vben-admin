package com.winus.express.modules.system.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoleDto {

    private String roleId;

    @NotBlank(message = "역할 코드는 필수입니다")
    @Size(max = 50, message = "역할 코드는 50자를 초과할 수 없습니다")
    private String roleCode;

    @NotBlank(message = "역할명은 필수입니다")
    @Size(max = 100, message = "역할명은 100자를 초과할 수 없습니다")
    private String roleName;

    @Size(max = 200, message = "설명은 200자를 초과할 수 없습니다")
    private String description;

    private String status;
    private Integer sortNo;
    private String remark;

    private List<String> menuIds;
}