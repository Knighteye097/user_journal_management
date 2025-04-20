package com.knighteye097.journal_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private static final String SECRET_KEY = "d4ea8ab22f00d691c74604e2fdcc83ab97e9f1aa32d0858847963180ee1fdbb818383fb33efe9225b518474cda7807a856655a29c76560d5c8fd40d00c7f0194";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractEmail(String token) {
        return extractAllClaims(token)
                .getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        final String username = extractEmail(token);
        return username.equals(email) && !isTokenExpired(token);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
