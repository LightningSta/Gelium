package org.example.gateway.Security.JWT;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

@Component
public class JWTtoken {


    private final Long RESPONSE_DURATION= 100000L;

    @Value("${token-value}")
    private String sign_key;

    @Value("${token-duration}")
    private Long duration;

    private String token;

    private Key key;

    public Key getKey() {
        return key;
    }

    public Key generateKey(){
        if (key == null) {
            try {
                byte[] keyBytes = new byte[32];
                new SecureRandom().nextBytes(keyBytes);
                this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate HMAC key", e);
            }
        }

        return key;
    }

    public String generateToken(Authentication auth) {
        JwtBuilder builder = Jwts.builder();
        builder.setExpiration(new Date(System.currentTimeMillis() + duration));
        builder.setIssuedAt(new Date(System.currentTimeMillis()));
        builder.setSubject(auth.getName());
        builder.claim("authorities", auth.getAuthorities());
        builder.signWith(generateKey());
        return builder.compact();
    }
    public String generateToken(String role) {
        JwtBuilder builder = Jwts.builder();
        builder.setExpiration(new Date(System.currentTimeMillis() + RESPONSE_DURATION));
        builder.setIssuedAt(new Date(System.currentTimeMillis()));
        builder.setSubject(role);
        List<GrantedAuthority> roles = List.of(new GrantedAuthority[]{new SimpleGrantedAuthority("ROLE_ADMIN")});
        builder.claim("authorities",roles);
        builder.signWith(generateKey());
        return builder.compact();
    }

}
