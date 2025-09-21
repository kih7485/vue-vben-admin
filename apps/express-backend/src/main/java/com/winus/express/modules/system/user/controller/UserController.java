package com.winus.express.modules.system.user.controller;

import com.winus.express.common.dto.ApiResponse;
import com.winus.express.common.dto.PageResponse;
import com.winus.express.modules.auth.dto.PasswordUpdateRequest;
import com.winus.express.modules.system.user.dto.UserDto;
import com.winus.express.modules.system.user.entity.User;
import com.winus.express.modules.system.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER_VIEW')")
    public ResponseEntity<PageResponse<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<User> users = keyword != null && !keyword.trim().isEmpty()
            ? userService.searchUsers(keyword.trim(), pageable)
            : userService.getUsers(pageable);

        PageResponse<User> response = PageResponse.<User>builder()
            .content(users.getContent())
            .totalElements(users.getTotalElements())
            .totalPages(users.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER_VIEW') or #userId == authentication.principal.userId")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER_CREATE')")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            User createdUser = userService.createUser(userDto);
            return ResponseEntity.ok(ApiResponse.success("사용자가 성공적으로 생성되었습니다.", createdUser));
        } catch (RuntimeException e) {
            log.error("Failed to create user: {}", userDto.getUserName(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER_UPDATE') or #userId == authentication.principal.userId")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserDto userDto) {
        try {
            User updatedUser = userService.updateUser(userId, userDto);
            return ResponseEntity.ok(ApiResponse.success("사용자 정보가 성공적으로 업데이트되었습니다.", updatedUser));
        } catch (RuntimeException e) {
            log.error("Failed to update user: {}", userId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(ApiResponse.success("사용자가 성공적으로 삭제되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to delete user: {}", userId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/department/{deptCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER_VIEW')")
    public ResponseEntity<List<User>> getUsersByDepartment(@PathVariable String deptCode) {
        List<User> users = userService.getUsersByDepartment(deptCode);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable String userId,
            @Valid @RequestBody PasswordUpdateRequest request) {
        try {
            userService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to update password for user: {}", userId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable String userId,
            @RequestBody String newPassword) {
        try {
            userService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 초기화되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to reset password for user: {}", userId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{userId}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> lockUser(@PathVariable String userId) {
        try {
            userService.lockUser(userId);
            return ResponseEntity.ok(ApiResponse.success("사용자 계정이 잠금되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to lock user: {}", userId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{userId}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> unlockUser(@PathVariable String userId) {
        try {
            userService.unlockUser(userId);
            return ResponseEntity.ok(ApiResponse.success("사용자 계정 잠금이 해제되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to unlock user: {}", userId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateUserRoles(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        try {
            userService.updateUserRoles(userId, roleIds);
            return ResponseEntity.ok(ApiResponse.success("사용자 역할이 성공적으로 업데이트되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to update user roles: {}", userId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{userId}/permissions")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<List<String>> getUserPermissions(@PathVariable String userId) {
        List<String> permissions = userService.getUserPermissions(userId);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/check/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}