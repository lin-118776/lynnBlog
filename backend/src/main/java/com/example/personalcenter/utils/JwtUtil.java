package com.example.personalcenter.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类：生成 Token、解析 Token、获取用户ID
 */
@Component
public class JwtUtil {

    /** 签名密钥（取自 application.yml） */
    @Value("${jwt.secret}")
    private String secret;

    /** 过期时间（毫秒，取自 application.yml） */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 根据配置的密钥构建签名 Key（HS256 要求密钥长度 >= 256 bit）
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token（仅携带用户ID）
     */
    public String generateToken(Long userId) {
        return generateToken(userId, null);
    }

    /**
     * 生成 Token（携带用户ID与用户名）
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token，返回 Claims；Token 无效或过期时抛出异常
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中获取用户ID；Token 无效时返回 null
     */
    public Long getUserId(String token) {
        try {
            Claims claims = parseToken(token);
            return Long.valueOf(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 校验 Token 是否有效（格式正确且未过期）
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}