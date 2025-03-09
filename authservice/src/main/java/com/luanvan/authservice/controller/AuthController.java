package com.luanvan.authservice.controller;

import com.luanvan.authservice.dto.LoginModel;
import com.luanvan.authservice.utils.JwtUtil;
import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.queries.GetUserQuery;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.commonservice.model.response.UserResponseModel;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

        if(!userResponse.getActive()){
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
        refreshTokenCookie.setPath("/api/v1/auth/refresh");
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
        if (refreshToken == null || refreshToken.isEmpty() || !jwtUtil.validateToken(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

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


}
