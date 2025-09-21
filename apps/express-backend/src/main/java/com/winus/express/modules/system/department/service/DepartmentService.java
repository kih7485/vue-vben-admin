package com.winus.express.modules.system.department.service;

import com.winus.express.modules.system.department.dto.DepartmentDto;
import com.winus.express.modules.system.department.entity.Department;

import java.util.List;
import java.util.Optional;

/**
 * Department Service Interface
 */
public interface DepartmentService {

    /**
     * Create a new department
     */
    Department createDepartment(DepartmentDto departmentDto);

    /**
     * Update an existing department
     */
    Department updateDepartment(String deptId, DepartmentDto departmentDto);

    /**
     * Delete a department (soft delete)
     */
    void deleteDepartment(String deptId);

    /**
     * Get department by ID
     */
    Optional<Department> getDepartmentById(String deptId);

    /**
     * Get department by code
     */
    Optional<Department> getDepartmentByCode(String deptCode);

    /**
     * Get all departments
     */
    List<Department> getAllDepartments();

    /**
     * Get department tree structure
     */
    List<Department> getDepartmentTree();

    /**
     * Get departments by parent ID
     */
    List<Department> getDepartmentsByParentId(String parentId);

    /**
     * Search departments by keyword
     */
    List<Department> searchDepartments(String keyword);

    /**
     * Check if department code exists
     */
    boolean existsByDeptCode(String deptCode, String excludeDeptId);

    /**
     * Update department sort order
     */
    void updateDepartmentSort(String deptId, int sortNo);

    /**
     * Get user count by department
     */
    long getUserCountByDepartment(String deptId);

    /**
     * Build department tree from flat list
     */
    List<Department> buildDepartmentTree(List<Department> departments);
}