package com.winus.express.modules.system.department.service;

import com.winus.express.modules.system.department.dto.DepartmentDto;
import com.winus.express.modules.system.department.entity.Department;
import com.winus.express.modules.system.department.repository.DepartmentRepository;
import com.winus.express.modules.system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Department Service Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Department createDepartment(DepartmentDto departmentDto) {
        // Check if department code already exists
        if (departmentRepository.existsByDeptCodeAndDelFlag(departmentDto.getDeptCode(), "0")) {
            throw new RuntimeException("부서 코드가 이미 존재합니다: " + departmentDto.getDeptCode());
        }

        Department department = new Department();
        department.setDeptId(UUID.randomUUID().toString());
        department.setDeptCode(departmentDto.getDeptCode());
        department.setDeptName(departmentDto.getDeptName());
        department.setParentId(departmentDto.getParentId() != null ? departmentDto.getParentId() : "0");
        department.setLeader(departmentDto.getLeader());
        department.setPhone(departmentDto.getPhone());
        department.setEmail(departmentDto.getEmail());
        department.setSortNo(departmentDto.getSortNo() != null ? departmentDto.getSortNo() : 0);
        department.setStatus(departmentDto.getStatus() != null ? departmentDto.getStatus() : "1");
        department.setRemark(departmentDto.getRemark());
        department.setCreateBy("system"); // TODO: Get from security context
        department.setDelFlag("0");

        // Set ancestors path
        if (!"0".equals(department.getParentId())) {
            Optional<Department> parentOpt = departmentRepository.findById(department.getParentId());
            if (parentOpt.isPresent()) {
                Department parent = parentOpt.get();
                String ancestors = parent.getAncestors() + "," + parent.getDeptId();
                department.setAncestors(ancestors);
            }
        } else {
            department.setAncestors("0");
        }

        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public Department updateDepartment(String deptId, DepartmentDto departmentDto) {
        Department department = departmentRepository.findById(deptId)
            .orElseThrow(() -> new RuntimeException("부서를 찾을 수 없습니다: " + deptId));

        // Check if department code is being changed and already exists
        if (departmentDto.getDeptCode() != null && !departmentDto.getDeptCode().equals(department.getDeptCode())) {
            if (departmentRepository.existsByDeptCodeAndDelFlag(departmentDto.getDeptCode(), "0")) {
                throw new RuntimeException("부서 코드가 이미 존재합니다: " + departmentDto.getDeptCode());
            }
            department.setDeptCode(departmentDto.getDeptCode());
        }

        if (departmentDto.getDeptName() != null) {
            department.setDeptName(departmentDto.getDeptName());
        }
        if (departmentDto.getParentId() != null && !departmentDto.getParentId().equals(department.getParentId())) {
            // Update parent and ancestors
            department.setParentId(departmentDto.getParentId());
            if (!"0".equals(departmentDto.getParentId())) {
                Optional<Department> parentOpt = departmentRepository.findById(departmentDto.getParentId());
                if (parentOpt.isPresent()) {
                    Department parent = parentOpt.get();
                    String ancestors = parent.getAncestors() + "," + parent.getDeptId();
                    department.setAncestors(ancestors);
                }
            } else {
                department.setAncestors("0");
            }
        }
        if (departmentDto.getLeader() != null) {
            department.setLeader(departmentDto.getLeader());
        }
        if (departmentDto.getPhone() != null) {
            department.setPhone(departmentDto.getPhone());
        }
        if (departmentDto.getEmail() != null) {
            department.setEmail(departmentDto.getEmail());
        }
        if (departmentDto.getSortNo() != null) {
            department.setSortNo(departmentDto.getSortNo());
        }
        if (departmentDto.getStatus() != null) {
            department.setStatus(departmentDto.getStatus());
        }
        if (departmentDto.getRemark() != null) {
            department.setRemark(departmentDto.getRemark());
        }

        department.setUpdateBy("system"); // TODO: Get from security context
        department.setUpdateTime(LocalDateTime.now());

        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(String deptId) {
        Department department = departmentRepository.findById(deptId)
            .orElseThrow(() -> new RuntimeException("부서를 찾을 수 없습니다: " + deptId));

        // Check if department has sub-departments
        List<Department> children = departmentRepository.findByParentCodeAndStatusAndDelFlagOrderBySortNo(
            department.getDeptCode(), "1", "0");
        if (!children.isEmpty()) {
            throw new RuntimeException("하위 부서가 있는 부서는 삭제할 수 없습니다");
        }

        // Check if department has users
        long userCount = userRepository.countByDeptCodeAndDelFlag(department.getDeptCode(), "0");
        if (userCount > 0) {
            throw new RuntimeException("소속 사용자가 있는 부서는 삭제할 수 없습니다");
        }

        department.setDelFlag("1");
        department.setUpdateBy("system"); // TODO: Get from security context
        department.setUpdateTime(LocalDateTime.now());

        departmentRepository.save(department);
    }

    @Override
    public Optional<Department> getDepartmentById(String deptId) {
        return departmentRepository.findByDeptIdAndDelFlag(deptId, "0");
    }

    @Override
    public Optional<Department> getDepartmentByCode(String deptCode) {
        return departmentRepository.findByDeptCodeAndDelFlag(deptCode, "0");
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findByStatusAndDelFlagOrderBySortNo("1", "0");
    }

    @Override
    public List<Department> getDepartmentTree() {
        List<Department> allDepartments = getAllDepartments();
        return buildDepartmentTree(allDepartments);
    }

    @Override
    public List<Department> getDepartmentsByParentId(String parentId) {
        String parentCode = "0";
        if (parentId != null && !"0".equals(parentId)) {
            Optional<Department> parentOpt = departmentRepository.findById(parentId);
            if (parentOpt.isPresent()) {
                parentCode = parentOpt.get().getDeptCode();
            }
        }
        return departmentRepository.findByParentCodeAndStatusAndDelFlagOrderBySortNo(
            parentCode, "1", "0");
    }

    @Override
    public List<Department> searchDepartments(String keyword) {
        return departmentRepository.searchDepartments(keyword, "0");
    }

    @Override
    public boolean existsByDeptCode(String deptCode, String excludeDeptId) {
        boolean exists = departmentRepository.existsByDeptCodeAndDelFlag(deptCode, "0");

        if (exists && excludeDeptId != null) {
            Optional<Department> dept = departmentRepository.findByDeptCodeAndDelFlag(deptCode, "0");
            if (dept.isPresent() && dept.get().getDeptId().equals(excludeDeptId)) {
                exists = false;
            }
        }

        return exists;
    }

    @Override
    @Transactional
    public void updateDepartmentSort(String deptId, int sortNo) {
        Department department = departmentRepository.findById(deptId)
            .orElseThrow(() -> new RuntimeException("부서를 찾을 수 없습니다: " + deptId));

        department.setSortNo(sortNo);
        department.setUpdateTime(LocalDateTime.now());
        departmentRepository.save(department);
    }

    @Override
    public long getUserCountByDepartment(String deptId) {
        Department department = departmentRepository.findById(deptId)
            .orElseThrow(() -> new RuntimeException("부서를 찾을 수 없습니다: " + deptId));

        return userRepository.countByDeptCodeAndDelFlag(department.getDeptCode(), "0");
    }

    @Override
    public List<Department> buildDepartmentTree(List<Department> departments) {
        if (departments == null || departments.isEmpty()) {
            return new ArrayList<>();
        }

        // Group departments by parent ID
        Map<String, List<Department>> deptMap = departments.stream()
            .collect(Collectors.groupingBy(
                dept -> dept.getParentId() != null ? dept.getParentId() : "0"
            ));

        // Build tree structure
        List<Department> rootDepartments = deptMap.getOrDefault("0", new ArrayList<>());

        for (Department rootDept : rootDepartments) {
            buildChildren(rootDept, deptMap);
        }

        // Sort by sortNo
        rootDepartments.sort(Comparator.comparing(Department::getSortNo));

        return rootDepartments;
    }

    private void buildChildren(Department parent, Map<String, List<Department>> deptMap) {
        List<Department> children = deptMap.get(parent.getDeptId());

        if (children != null && !children.isEmpty()) {
            children.sort(Comparator.comparing(Department::getSortNo));
            parent.setChildren(children);

            for (Department child : children) {
                buildChildren(child, deptMap);
            }
        }
    }
}