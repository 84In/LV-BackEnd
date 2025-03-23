package com.luanvan.commonservice.configuration;

import com.luanvan.commonservice.services.RedisService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;

public class RedisJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final RedisService redisService;
    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    public RedisJwtAuthenticationConverter(RedisService redisService) {
        this.redisService = redisService;
        this.grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        this.grantedAuthoritiesConverter.setAuthoritiesClaimName("role");
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        String token = jwt.getTokenValue();

        // 🔴 Kiểm tra nếu token đã bị logout trong Redis
        if (StringUtils.hasText(token) && redisService.isTokenLoggedOut(token)) {
            throw new BadCredentialsException("Token has been logged out");
        }

        // 🔹 Chuyển đổi quyền từ JWT và trả về danh sách GrantedAuthority
        return grantedAuthoritiesConverter.convert(jwt);
    }
}