package com.jp.auth.config;

import com.jp.auth.model.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@Log4j2
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    public String createToken(UserEntity user) {
        Map<String, Object> claims = Jwts.claims()
                .setSubject(user.getUsername());
        claims.put("id", user.getId());
        Date now = new Date();

        Date expiration = new Date(now.getTime() + (1000 * 60 * 60));  // 1 hora
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec();
            return Jwts.builder()
                    .setSubject(user.getUsername())
                    .setIssuedAt(now)
                    .setClaims(claims)
                    .setExpiration(expiration)
                    .signWith(secretKeySpec)
                    .compact();

        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new ResponseStatusException(UNAUTHORIZED);
        }
    }

    private SecretKeySpec getSecretKeySpec() throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        String encode = Encoders.BASE64.encode(secret.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec secretKeySpec = new SecretKeySpec(encode.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        return secretKeySpec;
    }

    public void validate(String token) {
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec();
            Jwts.parserBuilder()
                    .setSigningKey(secretKeySpec)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(UNAUTHORIZED);
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            SecretKeySpec secretKeySpec = getSecretKeySpec();
            return Jwts.parserBuilder()
                    .setSigningKey(secretKeySpec)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid token ");
        }
    }
}