package com.example.VTM.server.JwtTokenConfig;




import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    private final String SECRET_KEY = "this-is-a-very-secure-key-that-is-at-least-64-bytes-long-12345678!@#$";
    private final Key secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

    private final Long expirationTime = 30 * 24 * 60 * 60L;

    public String generateToken(UserDetails userDetails, Long id, String email, String contact) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id); // ✅ Add userId to token
        claims.put("email", email); // Add email to token
        claims.put("contact", contact); // Add contact to token



        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername()) // typically the email or username
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateToken(UserDetails userDetails, Long id, String email, String contact, String socialMedia) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id);
        claims.put("email", email);
        claims.put("contact", contact);
        claims.put("socialMedia", socialMedia);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }


    public boolean validateToken(String token, UserDetails userDetails){
        String username = extractUsername(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token){
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }
    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("email", String.class);  // Extract email from the claims
    }

    public String extractContact(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("contact", String.class);  // Extract contact from the claims
    }
    public Date extractExpiration(String token){
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getExpiration();
    }

    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

}
