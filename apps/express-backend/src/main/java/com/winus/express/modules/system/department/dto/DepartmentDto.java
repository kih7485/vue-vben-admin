package com.winus.express.modules.system.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentDto {

    private String deptId;

    @NotBlank(message = "부서 코드는 필수입니다")
    @Size(max = 50, message = "부서 코드는 50자를 초과할 수 없습니다")
    private String deptCode;

    @NotBlank(message = "부서명은 필수입니다")
    @Size(max = 100, message = "부서명은 100자를 초과할 수 없습니다")
    private String deptName;

    private String parentId;

    @Size(max = 100, message = "부서장명은 100자를 초과할 수 없습니다")
    private String leader;

    @Size(max = 20, message = "연락처는 20자를 초과할 수 없습니다")
    private String phone;

    @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
    private String email;

    private Integer sortNo;

    private String status;

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String remark;
}