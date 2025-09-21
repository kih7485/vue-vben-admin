package com.winus.express.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateRequest {

    @NotBlank(message = "기존 비밀번호는 필수입니다")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Size(min = 6, max = 20, message = "비밀번호는 6-20자 사이여야 합니다")
    private String newPassword;
}