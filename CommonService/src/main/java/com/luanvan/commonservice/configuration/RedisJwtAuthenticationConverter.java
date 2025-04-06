package com.luanvan.commonservice.configuration;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.StringUtils;

import com.luanvan.commonservice.services.RedisService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
            log.info(token + "redis");
            throw new BadCredentialsException("Token has been logged out");
        }
        // 🔹 Chuyển đổi quyền từ JWT và trả về danh sách GrantedAuthority
        return grantedAuthoritiesConverter.convert(jwt);
    }
}