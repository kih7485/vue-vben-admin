package com.winus.express.modules.system.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuDto {

    private String menuId;

    @NotBlank(message = "메뉴명은 필수입니다")
    @Size(max = 100, message = "메뉴명은 100자를 초과할 수 없습니다")
    private String menuName;

    private String parentId;

    @Size(max = 200, message = "메뉴 경로는 200자를 초과할 수 없습니다")
    private String menuPath;

    @Size(max = 200, message = "컴포넌트 경로는 200자를 초과할 수 없습니다")
    private String component;

    private String menuType;

    private String permission;

    @Size(max = 100, message = "아이콘은 100자를 초과할 수 없습니다")
    private String icon;

    private Integer sortNo;

    private String visible;

    private String status;

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    private String remark;
}