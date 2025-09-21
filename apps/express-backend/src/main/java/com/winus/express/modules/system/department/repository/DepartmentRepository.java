package com.winus.express.modules.system.department.repository;

import com.winus.express.modules.system.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Department Repository
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    /**
     * Find all active departments ordered by sort number
     */
    List<Department> findByStatusAndDelFlagOrderBySortNo(String status, String delFlag);

    /**
     * Find departments by parent code
     */
    List<Department> findByParentCodeAndStatusAndDelFlagOrderBySortNo(String parentCode, String status, String delFlag);

    /**
     * Check if department code exists
     */
    boolean existsByDeptCodeAndDelFlag(String deptCode, String delFlag);

    /**
     * Search departments by keyword
     */
    @Query("SELECT d FROM Department d WHERE d.delFlag = :delFlag AND " +
           "(d.deptName LIKE %:keyword% OR d.deptCode LIKE %:keyword%)")
    List<Department> searchDepartments(@Param("keyword") String keyword, @Param("delFlag") String delFlag);

    /**
     * Find department tree structure
     */
    @Query("SELECT d FROM Department d WHERE d.status = '1' AND d.delFlag = '0' ORDER BY d.sortNo")
    List<Department> findDepartmentTree();

    /**
     * Find departments by leader
     */
    Optional<Department> findByLeaderAndDelFlag(String leader, String delFlag);

    /**
     * Count child departments
     */
    long countByParentCodeAndStatusAndDelFlag(String parentCode, String status, String delFlag);

    /**
     * Find all ancestor departments by department code
     * Note: This method will need to be implemented in the service layer
     * using Java code to split the ancestors string due to HQL limitations
     */
    @Query("SELECT d FROM Department d WHERE d.delFlag = '0' AND d.deptCode = :deptCode")
    Optional<Department> findByDeptCodeForAncestor(@Param("deptCode") String deptCode);

    /**
     * Count departments by status
     */
    long countByStatusAndDelFlag(String status, String delFlag);

    /**
     * Find department by deptId and delFlag
     */
    Optional<Department> findByDeptIdAndDelFlag(String deptId, String delFlag);

    /**
     * Find department by deptCode and delFlag
     */
    Optional<Department> findByDeptCodeAndDelFlag(String deptCode, String delFlag);
}
