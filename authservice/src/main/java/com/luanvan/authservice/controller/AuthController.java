package com.luanvan.authservice.controller;

import com.luanvan.authservice.dto.LoginModel;
import com.luanvan.authservice.utils.JwtUtil;
import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.queries.GetUserQuery;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.services.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private QueryGateway queryGateway;

    @Autowired
    private RedisService redisService;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginModel loginModel, HttpServletResponse response) {
        UserResponseModel userResponse = queryGateway.query(
                        new GetUserQuery(loginModel.getUsername()),
                        ResponseTypes.instanceOf(UserResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                })
                .join();
        if (userResponse == null || userResponse.getUsername() == null || userResponse.getRole() == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (userResponse == null || !passwordEncoder.matches(loginModel.getPassword(), userResponse.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }

        if (!userResponse.getActive()) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        log.info(userResponse.toString());

        log.info("Login success");
//        "accestoken";
//        "cookie";
        Map<String, Object> claims = new HashMap<>();
        if (userResponse.getRole() != null) {
            claims.put("role", userResponse.getRole().getName());
        }

        String accessToken = jwtUtil.generateToken(userResponse.getUsername(), claims, false);
        String refreshToken = jwtUtil.generateToken(userResponse.getUsername(), claims, true);

        log.info("Login success, access_token:{}, refresh_token:{}", accessToken, refreshToken);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); //setFalse để debug
        refreshTokenCookie.setPath("/api/v1/auth");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); //7day
        response.addCookie(refreshTokenCookie);

        Map<String, String> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("username", userResponse.getUsername());

        return ApiResponse.builder()
                .code(0)
                .message("Login success")
                .data(result)
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        // if (refreshToken == null || refreshToken.isEmpty() || !jwtUtil.validateToken(refreshToken)) {
        //     throw new AppException(ErrorCode.INVALID_KEY);
        // }
        //Dont use, because it need secure by method https and config

        String username = jwtUtil.extractClaims(refreshToken).getSubject();
        String role = (String) jwtUtil.extractClaims(refreshToken).get("role");

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        String accessToken = jwtUtil.generateToken(username, claims, false);

        Map<String, String> result = new HashMap<>();
        result.put("accessToken", accessToken);

        return ApiResponse.builder()
                .code(0)
                .message("Refresh success")
                .data(result)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = getRefreshTokenFromCookie(request);

        log.info("Logout success, access_token:{}, refresh_token:{}", accessToken, refreshToken);

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7); // Loại bỏ "Bearer " để lấy token thực

            // Lấy thời gian hết hạn của token
            Instant accessTokenExpiry = jwtUtil.getTokenExpiration(accessToken);
            Instant refreshTokenExpiry = jwtUtil.getTokenExpiration(refreshToken);

            long accessTokenTtl = Math.max(0, accessTokenExpiry.getEpochSecond() - Instant.now().getEpochSecond());
            long refreshTokenTtl = Math.max(0, refreshTokenExpiry.getEpochSecond() - Instant.now().getEpochSecond());

            // Lưu vào Redis với TTL
            redisService.storeToken(accessToken, accessTokenTtl);
            redisService.storeToken(refreshToken, refreshTokenTtl);

            // Xóa thông tin đăng nhập khỏi SecurityContext
            SecurityContextHolder.clearContext();

            // Xóa refresh token trên trình duyệt bằng Set-Cookie
            return ResponseEntity.ok()
                    .header("Set-Cookie", "refreshToken=; HttpOnly; SameSite=Strict; Max-Age=0; Path=/")
                    .body(ApiResponse.builder()
                            .message("Logout success!")
                            .code(0)
                            .build());
        }

        return ResponseEntity.badRequest()
                .body(ApiResponse.builder()
                        .message("Logout failed, because invalid token!")
                        .code(1)
                        .build());
    }


    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


}
