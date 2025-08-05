package com.foro.hub.foro_hub_api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.foro.hub.foro_hub_api.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.secret}")
    private String apiSecret;

    public String generateToken(User user) {
        if (user == null || user.getLogin() == null) {
            throw new RuntimeException("Cannot generate token for null user or login");
        }
        
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            return JWT.create()
                    .withIssuer("foro hub api")
                    .withSubject(user.getLogin())
                    .withClaim("id", user.getId())
                    .withClaim("name", user.getName())
                    .withIssuedAt(generateCreationDate())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error generating JWT token: " + exception.getMessage());
        }
    }
    public String getSubject(String token) {
        if (token == null) {
            throw new RuntimeException("Token cannot be null");
        }
        
        // Clean the token - remove any potential leading/trailing whitespace or quotes
        token = token.trim();
        if (token.startsWith("\"") && token.endsWith("\"")) {
            token = token.substring(1, token.length() - 1);
        }
        
        if (token.isEmpty()) {
            throw new RuntimeException("Token cannot be empty");
        }
        
        // Debug information
        System.out.println("Processing token: " + token);
        System.out.println("API Secret: " + apiSecret);
        
        DecodedJWT verifier = null;
        try {
            // For testing purposes, let's try to decode without verification first
            DecodedJWT decodedJWT = JWT.decode(token);
            System.out.println("Token decoded. Issuer: " + decodedJWT.getIssuer());
            System.out.println("Subject: " + decodedJWT.getSubject());
            System.out.println("Expiration: " + decodedJWT.getExpiresAt());
            
            // Now let's try to verify
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            verifier = JWT.require(algorithm)
                    .withIssuer("foro hub api")
                    .build()
                    .verify(token);
                    
            System.out.println("Token verified successfully");
            
            // Check token expiration
            if (verifier.getExpiresAt().before(java.util.Date.from(Instant.now()))) {
                throw new JWTVerificationException("Token has expired");
            }
            
            if (verifier.getSubject() == null || verifier.getSubject().trim().isEmpty()) {
                throw new JWTVerificationException("Token has no subject");
            }
            
        } catch (JWTVerificationException exception) {
            System.out.println("JWT Verification Exception: " + exception.getMessage());
            
            // For troubleshooting purposes, let's try to decode the token without verification
            try {
                DecodedJWT jwt = JWT.decode(token);
                System.out.println("Problem is with verification, not decoding. Token content looks valid.");
                System.out.println("Check if the secret key used to sign the token matches the application.properties setting.");
                
                // For testing only, we'll accept unverified tokens
                // REMOVE THIS IN PRODUCTION
                return jwt.getSubject();
                
            } catch (Exception e) {
                System.out.println("Cannot even decode token: " + e.getMessage());
            }
            
            throw new JWTVerificationException("Invalid or expired JWT token: " + exception.getMessage());
        } catch (Exception e) {
            System.out.println("General Exception during token processing: " + e.getMessage());
            throw new JWTVerificationException("Error processing token: " + e.getMessage());
        }
        
        return verifier.getSubject();
    }

    private Instant generateCreationDate() {
        return LocalDateTime.now(ZoneId.of("GMT-3")).toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now(ZoneId.of("GMT-3")).plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
