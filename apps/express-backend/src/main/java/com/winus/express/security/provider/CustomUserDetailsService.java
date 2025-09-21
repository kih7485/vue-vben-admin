package com.winus.express.security.provider;

import com.winus.express.modules.system.role.entity.Role;
import com.winus.express.modules.system.user.entity.User;
import com.winus.express.modules.system.user.service.UserService;
import com.winus.express.security.principal.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Custom User Details Service for Spring Security
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return CustomUserPrincipal.builder()
            .userId(user.getUserId())
            .username(user.getUserName())
            .password(user.getPassword())
            .email(user.getEmail())
            .realName(user.getRealName())
            .authorities(mapRolesToAuthorities(user.getRoles()))
            .enabled(user.isEnabled())
            .accountNonExpired(true)
            .accountNonLocked(user.isAccountNonLocked())
            .credentialsNonExpired(true)
            .build();
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
            .filter(Role::isActive)
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode().toUpperCase()))
            .collect(Collectors.toList());
    }
}