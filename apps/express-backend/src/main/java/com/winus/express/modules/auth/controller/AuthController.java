package com.winus.express.modules.auth.controller;

import com.winus.express.modules.auth.dto.LoginRequest;
import com.winus.express.modules.auth.dto.LoginResponse;
import com.winus.express.modules.system.user.dto.UserDto;
import com.winus.express.security.principal.CustomUserPrincipal;
import com.winus.express.modules.system.user.service.UserService;
import com.winus.express.common.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();

            String accessToken = jwtUtil.generateAccessToken(userPrincipal.getUserId());
            String refreshToken = jwtUtil.generateRefreshToken(userPrincipal.getUserId());

            // Update login info
            userService.updateLoginInfo(userPrincipal.getUserId(), loginRequest.getClientIp());

            LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration())
                .user(UserDto.builder()
                    .userId(userPrincipal.getUserId())
                    .userName(userPrincipal.getUsername())
                    .email(userPrincipal.getEmail())
                    .realName(userPrincipal.getRealName())
                    .build())
                .permissions(userService.getUserPermissions(userPrincipal.getUserId()))
                .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (jwtUtil.validateToken(refreshToken)) {
            String userId = jwtUtil.getUserIdFromToken(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(userId);

            return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken,
                "tokenType", "Bearer",
                "expiresIn", String.valueOf(jwtUtil.getAccessTokenExpiration())
            ));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal userPrincipal) {
            UserDto userDto = UserDto.builder()
                .userId(userPrincipal.getUserId())
                .userName(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .realName(userPrincipal.getRealName())
                .build();

            return ResponseEntity.ok(userDto);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/codes")
    public ResponseEntity<List<String>> getAccessCodes(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal userPrincipal) {
            List<String> permissions = userService.getUserPermissions(userPrincipal.getUserId());
            return ResponseEntity.ok(permissions);
        }

        return ResponseEntity.ok(List.of());
    }
}