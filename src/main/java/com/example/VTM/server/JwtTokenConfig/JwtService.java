package com.example.VTM.server.JwtTokenConfig;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static Key secretKey;

    // Token validity (e.g., 1 day in milliseconds)
    private final Long expirationTime = 30 * 24 * 60 * 60L;

    private static final String SECRET = "this-is-a-very-secure-key-that-is-at-least-64-bytes-long-12345678!@#$";

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }



    /**
     * Generate a JWT token with the userId as a claim
     */


//    public String generateToken(UserDetails userDetails){
//        Map<String, Object> claims = new HashMap<>();
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
//                .signWith(secretKey, SignatureAlgorithm.HS512)
//                .compact();
//    }

    public static Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            return claims.get("userId", Long.class);
        } catch (Exception e) {
            e.printStackTrace(); // You can log instead
            throw new RuntimeException("Invalid or expired JWT token");
        }
    }

    public static Map<String, Object> getUserDetailsFromToken(String token) {
        try {
            // Parse the token and get claims
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Retrieve user details from claims
            String username = claims.getSubject(); // username (subject)
            String email = claims.get("email", String.class); // Custom email claim
            String contactNumber = claims.get("contact", String.class); // Custom contactNumber claim

            // Prepare and return user details
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("username", username);
            userDetails.put("email", email);
            userDetails.put("contact", contactNumber);

            return userDetails;
        } catch (Exception e) {
            e.printStackTrace(); // You can log instead
            throw new RuntimeException("Invalid or expired JWT token");
        }
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
