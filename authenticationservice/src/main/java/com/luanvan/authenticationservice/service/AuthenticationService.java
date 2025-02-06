package com.luanvan.authenticationservice.service;

import com.luanvan.authenticationservice.dto.LoginRequest;
import com.luanvan.commonservice.model.UserResponseModel;
import com.luanvan.commonservice.queries.GetUserQuery;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.util.CookieGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.interfaces.RSAPublicKey;
import java.security.PublicKey;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.public-key-uri}")
    private String publicKeyUri;

    private final String keycloakAdminUrl = "http://localhost:8080/auth/admin/realms/{realm}/users";

    @Autowired
    private QueryGateway queryGateway;

    private RestTemplate restTemplate;
    private NimbusJwtDecoder jwtDecoder;

    public Map<String, Object> login(LoginRequest loginRequest, HttpServletResponse response) {
        // Kiểm tra xem người dùng có tồn tại hay không

        GetUserQuery getUserQuery = new GetUserQuery(loginRequest.getUsername());
        UserResponseModel user = queryGateway.query(getUserQuery, ResponseTypes.instanceOf(UserResponseModel.class)).exceptionally(ex -> {
            throw new RuntimeException("Login failed");
        }).join();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!user.getActive() && !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Login failed"); // Hoặc trả về lỗi phù hợp
        }

        // Gửi yêu cầu xác thực tới Keycloak và lấy token
        Map<String, Object> tokens = authenticateWithKeycloak(loginRequest);

        // Lưu refresh token vào cookie
        saveRefreshTokenToCookie(tokens, response);

        // Trích xuất và trả về roles từ token (nếu có)
        List<String> roles = extractRolesFromToken(tokens);

        return Map.of("roles", roles, "access_token", tokens.get("access_token"));
    }

    private Map<String, Object> authenticateWithKeycloak(LoginRequest loginRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        String requestBody = String.format(
                "grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s",
                "api-gateway", // Client ID
                "your-client-secret", // Client Secret
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                entity,
                Map.class
        );

        return response.getBody();
    }

    private void saveRefreshTokenToCookie(Map<String, Object> tokens, HttpServletResponse response) {
        String refreshToken = (String) tokens.get("refresh_token");

        CookieGenerator cookieGenerator = new CookieGenerator();
        cookieGenerator.setCookieName("refresh_token");
        cookieGenerator.setCookieMaxAge(60 * 60 * 24 * 30); // 30 ngày
        cookieGenerator.setCookieHttpOnly(true);
        cookieGenerator.setCookieSecure(true); // Đảm bảo chỉ gửi qua HTTPS
        cookieGenerator.setCookiePath("/");

        cookieGenerator.addCookie(response, refreshToken);
    }

    // Trích xuất vai trò từ token JWT
    private List<String> extractRolesFromToken(Map<String, Object> tokens) {
        String accessToken = (String) tokens.get("access_token");
        Jwt jwt = jwtDecoder.decode(accessToken);
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess != null) {
            return (List<String>) realmAccess.get("roles");
        }
        return List.of(); // Nếu không có vai trò thì trả về danh sách trống
    }

    // Hàm lấy PublicKey từ URL
    private RSAPublicKey loadPublicKey(String publicKeyUri) throws Exception {
        URL url = new URL(publicKeyUri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder publicKey = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            publicKey.append(line);
        }

        String key = publicKey.toString()
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(key);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        return (RSAPublicKey) pubKey;
    }

    // Khởi tạo NimbusJwtDecoder với Public Key
    public void initJwtDecoder() throws Exception {
        RSAPublicKey publicKey = loadPublicKey(publicKeyUri);
        this.jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
    }


    @KafkaListener(topics = "login-create-keycloak", groupId = "authentication")
    public void createUserInKeycloak(String username, String password) {
        // Tạo một user JSON object cho Keycloak
        String userJson = String.format(
                "{\"username\": \"%s\", \"enabled\": true, \"credentials\": [{\"type\": \"password\", \"value\": \"%s\", \"temporary\": false}]}",
                username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(userJson, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                keycloakAdminUrl, HttpMethod.POST, request, String.class, "your-realm");

        if (response.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("User created successfully in Keycloak.");
        } else {
            System.out.println("Failed to create user in Keycloak.");
        }
    }

}
