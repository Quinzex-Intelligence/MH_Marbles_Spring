package com.qi.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private final long ACCESS_EXP = 1000 * 60 * 15;        // 15 min
    private final long REFRESH_EXP = 1000 * 60 * 60 * 24;  // 1 day
    private  SecretKey key ;
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generateAccessToken(String email,String role){
        return Jwts.builder()
                .setSubject(email)
                .claim("type","ACCESS")
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ACCESS_EXP))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .claim("type","REFRESH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+REFRESH_EXP))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token, String email){
        return extractEmail(token).equals(email)
                && extractTokenType(token).equals("ACCESS")
                && !isExpired(token);
    }
    public boolean validateRefreshToken(String token, String email){
        return extractEmail(token).equals(email)
                && extractTokenType(token).equals("REFRESH")
                && !isExpired(token);
    }

    public String extractTokenType(String token){
        return extractClaims(token).get("type",String.class);
    }
    public String extractEmail(String token){
        return extractClaims(token).getSubject();
    }
    public boolean isExpired(String token){
        return extractClaims(token).getExpiration().before(new Date());
    }

    public String extractRole(String token){
        return extractClaims(token).get("role",String.class);
    }
    private Claims extractClaims(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        }catch (Exception e){
            throw new RuntimeException("Invalid or expired token");
        }
    }
}
