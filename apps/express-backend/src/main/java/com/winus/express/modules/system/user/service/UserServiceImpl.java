package com.winus.express.modules.system.user.service;

import com.winus.express.modules.system.user.dto.UserDto;
import com.winus.express.modules.system.role.entity.Role;
import com.winus.express.modules.system.user.entity.User;
import com.winus.express.modules.system.menu.repository.MenuRepository;
import com.winus.express.modules.system.role.repository.RoleRepository;
import com.winus.express.modules.system.user.repository.UserRepository;
import com.winus.express.modules.system.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * User Service Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(UserDto userDto) {
        // Check if username already exists
        if (existsByUsername(userDto.getUserName())) {
            throw new RuntimeException("Username already exists: " + userDto.getUserName());
        }

        // Check if email already exists
        if (userDto.getEmail() != null && existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDto.getEmail());
        }

        User user = new User();
        user.setUserId(userDto.getUserId());
        user.setUserName(userDto.getUserName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRealName(userDto.getRealName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setDeptCode(userDto.getDeptCode());
        user.setStatus(userDto.getStatus() != null ? userDto.getStatus() : "1");
        user.setCreateBy("system"); // TODO: Get from security context

        // Set roles if provided
        if (userDto.getRoleIds() != null && !userDto.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleId : userDto.getRoleIds()) {
                roleRepository.findById(roleId).ifPresent(roles::add);
            }
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(String userId, UserDto userDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Check if email is being changed and already exists
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            if (existsByEmail(userDto.getEmail())) {
                throw new RuntimeException("Email already exists: " + userDto.getEmail());
            }
        }

        user.setRealName(userDto.getRealName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setDeptCode(userDto.getDeptCode());
        user.setStatus(userDto.getStatus());
        user.setUpdateBy("system"); // TODO: Get from security context
        user.setUpdateTime(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.setDelFlag("1");
        user.setUpdateBy("system"); // TODO: Get from security context
        user.setUpdateTime(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUserNameAndDelFlag(username, "0");
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailAndDelFlag(email, "0");
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findByStatusAndDelFlag("1", "0", pageable);
    }

    @Override
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.searchUsers(keyword, "0", pageable);
    }

    @Override
    public List<User> getUsersByDepartment(String deptCode) {
        return userRepository.findByDeptCodeAndDelFlagOrderBySortNo(deptCode, "0");
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUserNameAndDelFlag(username, "0");
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDelFlag(email, "0");
    }

    @Override
    @Transactional
    public void updatePassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        userRepository.updatePassword(userId, encodedPassword, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void resetPassword(String userId, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        userRepository.updatePassword(userId, encodedPassword, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void lockUser(String userId) {
        userRepository.updateLockStatus(userId, "1");
    }

    @Override
    @Transactional
    public void unlockUser(String userId) {
        userRepository.updateLockStatus(userId, "0");
    }

    @Override
    @Transactional
    public void updateUserRoles(String userId, List<String> roleIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Set<Role> roles = new HashSet<>();
        for (String roleId : roleIds) {
            roleRepository.findById(roleId).ifPresent(roles::add);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public List<String> getUserPermissions(String userId) {
        return menuRepository.findPermissionsByUserId(userId);
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        Optional<User> userOpt = getUserByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return user.isEnabled() && passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    @Transactional
    public void updateLoginInfo(String userId, String loginIp) {
        userRepository.updateLoginInfo(userId, LocalDateTime.now(), loginIp);
    }
}