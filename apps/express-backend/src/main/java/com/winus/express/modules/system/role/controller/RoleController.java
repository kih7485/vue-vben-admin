package com.winus.express.modules.system.role.controller;

import com.winus.express.common.dto.ApiResponse;
import com.winus.express.common.dto.PageResponse;
import com.winus.express.modules.system.role.dto.RoleDto;
import com.winus.express.modules.system.role.entity.Role;
import com.winus.express.modules.system.role.service.RoleService;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_VIEW')")
    public ResponseEntity<PageResponse<Role>> getRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Role> roles = keyword != null && !keyword.trim().isEmpty()
            ? roleService.searchRoles(keyword.trim(), pageable)
            : roleService.getRoles(pageable);

        PageResponse<Role> response = PageResponse.<Role>builder()
            .content(roles.getContent())
            .totalElements(roles.getTotalElements())
            .totalPages(roles.getTotalPages())
            .currentPage(page)
            .pageSize(size)
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_VIEW')")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllActiveRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_VIEW')")
    public ResponseEntity<Role> getRoleById(@PathVariable String roleId) {
        Optional<Role> role = roleService.getRoleById(roleId);
        return role.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_CREATE')")
    public ResponseEntity<ApiResponse<Role>> createRole(@Valid @RequestBody RoleDto roleDto) {
        try {
            Role createdRole = roleService.createRole(roleDto);
            return ResponseEntity.ok(ApiResponse.success("역할이 성공적으로 생성되었습니다.", createdRole));
        } catch (RuntimeException e) {
            log.error("Failed to create role: {}", roleDto.getRoleCode(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_UPDATE')")
    public ResponseEntity<ApiResponse<Role>> updateRole(
            @PathVariable String roleId,
            @Valid @RequestBody RoleDto roleDto) {
        try {
            Role updatedRole = roleService.updateRole(roleId, roleDto);
            return ResponseEntity.ok(ApiResponse.success("역할 정보가 성공적으로 업데이트되었습니다.", updatedRole));
        } catch (RuntimeException e) {
            log.error("Failed to update role: {}", roleId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable String roleId) {
        try {
            roleService.deleteRole(roleId);
            return ResponseEntity.ok(ApiResponse.success("역할이 성공적으로 삭제되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to delete role: {}", roleId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{roleId}/menus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateRoleMenus(
            @PathVariable String roleId,
            @RequestBody List<String> menuIds) {
        try {
            roleService.updateRoleMenus(roleId, menuIds);
            return ResponseEntity.ok(ApiResponse.success("역할 메뉴 권한이 성공적으로 업데이트되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to update role menus: {}", roleId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{roleId}/menus")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_VIEW')")
    public ResponseEntity<List<String>> getRoleMenus(@PathVariable String roleId) {
        List<String> menuIds = roleService.getRoleMenuIds(roleId);
        return ResponseEntity.ok(menuIds);
    }

    @GetMapping("/check/code/{roleCode}")
    public ResponseEntity<Boolean> checkRoleCodeExists(@PathVariable String roleCode) {
        boolean exists = roleService.existsByRoleCode(roleCode);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_VIEW')")
    public ResponseEntity<List<Role>> getUserRoles(@PathVariable String userId) {
        List<Role> roles = roleService.getUserRoles(userId);
        return ResponseEntity.ok(roles);
    }
}
