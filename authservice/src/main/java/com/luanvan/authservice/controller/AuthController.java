package com.luanvan.authservice.controller;

import com.luanvan.authservice.dto.LoginModel;
import com.luanvan.authservice.utils.JwtUtil;
import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.event.GetUserQuery;
import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.commonservice.model.UserResponseModel;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private QueryGateway queryGateway;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginModel loginModel, HttpServletResponse response) {
        UserResponseModel userResponse = queryGateway.query(
                        new GetUserQuery(loginModel.getUsername()),
                        ResponseTypes.instanceOf(UserResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                })
                .join();

        if(userResponse == null || !passwordEncoder.matches(loginModel.getPassword(), userResponse.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }

        String accessToken = jwtUtil.generateToken(userResponse.getUsername(), Map.of("role", userResponse.getRole().getName()),false);
        String refreshToken = jwtUtil.generateToken(userResponse.getUsername(), Map.of("role", userResponse.getRole().getName()),true);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/api/v1/auth/refresh");
        response.addCookie(refreshTokenCookie);

        return ApiResponse.builder()
                .code(200)
                .message("Login success")
                .data(Map.of("accessToken", accessToken))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty() || !jwtUtil.validateToken(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        String username = jwtUtil.extractClaims(refreshToken).getSubject();
        String role = (String) jwtUtil.extractClaims(refreshToken).get("role");

        String accessToken = jwtUtil.generateToken(username, Map.of("role", role),false);

        return ApiResponse.builder()
                .code(200)
                .message("Refresh success")
                .data(Map.of("accessToken", accessToken))
                .build();
    }


}
