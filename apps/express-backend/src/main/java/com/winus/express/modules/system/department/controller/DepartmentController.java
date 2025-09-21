package com.winus.express.modules.system.department.controller;

import com.winus.express.common.dto.ApiResponse;
import com.winus.express.modules.system.department.dto.DepartmentDto;
import com.winus.express.modules.system.department.entity.Department;
import com.winus.express.modules.system.department.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEPT_VIEW')")
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/tree")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEPT_VIEW')")
    public ResponseEntity<List<Department>> getDepartmentTree() {
        List<Department> departmentTree = departmentService.getDepartmentTree();
        return ResponseEntity.ok(departmentTree);
    }

    @GetMapping("/{deptId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEPT_VIEW')")
    public ResponseEntity<Department> getDepartmentById(@PathVariable String deptId) {
        Optional<Department> department = departmentService.getDepartmentById(deptId);
        return department.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEPT_CREATE')")
    public ResponseEntity<ApiResponse<Department>> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        try {
            Department createdDepartment = departmentService.createDepartment(departmentDto);
            return ResponseEntity.ok(ApiResponse.success("부서가 성공적으로 생성되었습니다.", createdDepartment));
        } catch (RuntimeException e) {
            log.error("Failed to create department: {}", departmentDto.getDeptName(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{deptId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEPT_UPDATE')")
    public ResponseEntity<ApiResponse<Department>> updateDepartment(
            @PathVariable String deptId,
            @Valid @RequestBody DepartmentDto departmentDto) {
        try {
            Department updatedDepartment = departmentService.updateDepartment(deptId, departmentDto);
            return ResponseEntity.ok(ApiResponse.success("부서 정보가 성공적으로 업데이트되었습니다.", updatedDepartment));
        } catch (RuntimeException e) {
            log.error("Failed to update department: {}", deptId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{deptId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEPT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable String deptId) {
        try {
            departmentService.deleteDepartment(deptId);
            return ResponseEntity.ok(ApiResponse.success("부서가 성공적으로 삭제되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to delete department: {}", deptId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEPT_VIEW')")
    public ResponseEntity<List<Department>> getDepartmentsByParent(@PathVariable String parentId) {
        List<Department> departments = departmentService.getDepartmentsByParentId(parentId);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/code/{deptCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEPT_VIEW')")
    public ResponseEntity<Department> getDepartmentByCode(@PathVariable String deptCode) {
        Optional<Department> department = departmentService.getDepartmentByCode(deptCode);
        return department.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{deptId}/sort")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateDepartmentSort(
            @PathVariable String deptId,
            @RequestParam int sortNo) {
        try {
            departmentService.updateDepartmentSort(deptId, sortNo);
            return ResponseEntity.ok(ApiResponse.success("부서 순서가 성공적으로 업데이트되었습니다.", null));
        } catch (RuntimeException e) {
            log.error("Failed to update department sort: {}", deptId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/check/code/{deptCode}")
    public ResponseEntity<Boolean> checkDepartmentCodeExists(
            @PathVariable String deptCode,
            @RequestParam(required = false) String excludeDeptId) {
        boolean exists = departmentService.existsByDeptCode(deptCode, excludeDeptId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEPT_VIEW')")
    public ResponseEntity<List<Department>> searchDepartments(@RequestParam String keyword) {
        List<Department> departments = departmentService.searchDepartments(keyword);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{deptId}/users/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DEPT_VIEW')")
    public ResponseEntity<Long> getDepartmentUserCount(@PathVariable String deptId) {
        long userCount = departmentService.getUserCountByDepartment(deptId);
        return ResponseEntity.ok(userCount);
    }
}