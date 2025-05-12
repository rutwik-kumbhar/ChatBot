package com.monocept.chatbot.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Base64;

@Slf4j
public class JwtUtil {
    public static boolean isTokenValid(String token) {
        log.info("isTokenValid : start : {}" ,token);
        try {
            byte[] decodedKey = Base64.getDecoder().decode("kRkBIlMMJTzkMlEuty3UDc/21E6DHRraZQgr/QUIZ8s=");
            SecretKey signingKey = Keys.hmacShaKeyFor(decodedKey);

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            // SignatureException, MalformedJwtException, ExpiredJwtException, etc.
            System.err.println("Invalid token: " + e.getMessage());
            return false;
        }
    }
}
