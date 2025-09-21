package com.winus.express.modules.system.role.service;

import com.winus.express.modules.system.role.dto.RoleDto;
import com.winus.express.modules.system.role.entity.Role;
import com.winus.express.modules.system.menu.entity.Menu;
import com.winus.express.modules.system.user.entity.User;
import com.winus.express.modules.system.role.repository.RoleRepository;
import com.winus.express.modules.system.menu.repository.MenuRepository;
import com.winus.express.modules.system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Role Service Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Role createRole(RoleDto roleDto) {
        // Check if role code already exists
        if (existsByRoleCode(roleDto.getRoleCode())) {
            throw new RuntimeException("역할 코드가 이미 존재합니다: " + roleDto.getRoleCode());
        }

        Role role = new Role();
        role.setRoleId(UUID.randomUUID().toString());
        role.setRoleCode(roleDto.getRoleCode());
        role.setRoleName(roleDto.getRoleName());
        role.setDescription(roleDto.getDescription());
        role.setStatus(roleDto.getStatus() != null ? roleDto.getStatus() : "1");
        role.setSortNo(roleDto.getSortNo() != null ? roleDto.getSortNo() : 0);
        role.setRemark(roleDto.getRemark());
        role.setCreateBy("system"); // TODO: Get from security context
        role.setDelFlag("0");

        // Set menus if provided
        if (roleDto.getMenuIds() != null && !roleDto.getMenuIds().isEmpty()) {
            Set<Menu> menus = new HashSet<>();
            for (String menuId : roleDto.getMenuIds()) {
                menuRepository.findById(menuId).ifPresent(menus::add);
            }
            role.setMenus(menus);
        }

        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role updateRole(String roleId, RoleDto roleDto) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("역할을 찾을 수 없습니다: " + roleId));

        // Check if role code is being changed and already exists
        if (roleDto.getRoleCode() != null && !roleDto.getRoleCode().equals(role.getRoleCode())) {
            if (existsByRoleCode(roleDto.getRoleCode())) {
                throw new RuntimeException("역할 코드가 이미 존재합니다: " + roleDto.getRoleCode());
            }
            role.setRoleCode(roleDto.getRoleCode());
        }

        if (roleDto.getRoleName() != null) {
            role.setRoleName(roleDto.getRoleName());
        }
        if (roleDto.getDescription() != null) {
            role.setDescription(roleDto.getDescription());
        }
        if (roleDto.getStatus() != null) {
            role.setStatus(roleDto.getStatus());
        }
        if (roleDto.getSortNo() != null) {
            role.setSortNo(roleDto.getSortNo());
        }
        if (roleDto.getRemark() != null) {
            role.setRemark(roleDto.getRemark());
        }

        role.setUpdateBy("system"); // TODO: Get from security context
        role.setUpdateTime(LocalDateTime.now());

        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(String roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("역할을 찾을 수 없습니다: " + roleId));

        // Check if role is assigned to any users
        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("사용자가 할당된 역할은 삭제할 수 없습니다");
        }

        role.setDelFlag("1");
        role.setUpdateBy("system"); // TODO: Get from security context
        role.setUpdateTime(LocalDateTime.now());

        roleRepository.save(role);
    }

    @Override
    public Optional<Role> getRoleById(String roleId) {
        return roleRepository.findByRoleIdAndDelFlag(roleId, "0");
    }

    @Override
    public Optional<Role> getRoleByCode(String roleCode) {
        return roleRepository.findByRoleCodeAndDelFlag(roleCode, "0");
    }

    @Override
    public List<Role> getAllActiveRoles() {
        return roleRepository.findByStatusAndDelFlagOrderBySortNo("1", "0");
    }

    @Override
    public Page<Role> getRoles(Pageable pageable) {
        return roleRepository.findByDelFlag("0", pageable);
    }

    @Override
    public Page<Role> searchRoles(String keyword, Pageable pageable) {
        return roleRepository.searchRoles(keyword, "0", pageable);
    }

    @Override
    public boolean existsByRoleCode(String roleCode) {
        return roleRepository.existsByRoleCodeAndDelFlag(roleCode, "0");
    }

    @Override
    @Transactional
    public void updateRoleMenus(String roleId, List<String> menuIds) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("역할을 찾을 수 없습니다: " + roleId));

        Set<Menu> menus = new HashSet<>();
        for (String menuId : menuIds) {
            menuRepository.findById(menuId).ifPresent(menus::add);
        }

        role.setMenus(menus);
        role.setUpdateTime(LocalDateTime.now());
        roleRepository.save(role);
    }

    @Override
    public List<String> getRoleMenuIds(String roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("역할을 찾을 수 없습니다: " + roleId));

        return role.getMenus().stream()
            .map(Menu::getMenuId)
            .collect(Collectors.toList());
    }

    @Override
    public List<Role> getUserRoles(String userId) {
        return roleRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void assignRolesToUser(String userId, List<String> roleIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        Set<Role> roles = new HashSet<>();
        for (String roleId : roleIds) {
            roleRepository.findById(roleId).ifPresent(roles::add);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(String userId, String roleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        user.getRoles().removeIf(role -> role.getRoleId().equals(roleId));
        userRepository.save(user);
    }

    @Override
    public List<String> getRolePermissions(String roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("역할을 찾을 수 없습니다: " + roleId));

        return role.getMenus().stream()
            .filter(menu -> menu.getPermission() != null && !menu.getPermission().isEmpty())
            .map(Menu::getPermission)
            .distinct()
            .collect(Collectors.toList());
    }
}