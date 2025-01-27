package com.learning.security.utils;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.learning.security.services.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtils {

    @Value("${yousuf.app.jwtSecret}")
    private String jwtSecret;

    @Value("${yousuf.app.jwtExpirationTimeInMs}")
    private int jwtExpirationTimeInMs;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    
    public String generateTokenByAuth(Authentication auth) {
        
        // Only valid authentication object should reach here
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        // https://github.com/jwtk/jjwt?tab=readme-ov-file#creating-a-jwt
        return Jwts.builder()
                .issuer("M. Yousuf")
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationTimeInMs))
                .signWith(getKey())
                .compact();
        

            // .header()
            //     .add("Subject", userDetails.getUsername())
            //     .add("Issued By", "M. Yousuf")
            //     .add("Issued At", new Date())
            //     .add("Expiration", new Date(new Date().getTime() + jwtExpirationTimeInMs))
            // .and()
            // .signWith(getKey(), Jwts.SIG.HS256)
        // It is usually recommended to specify the signing key by calling the JwtBuilder's signWith method and let JJWT determine the most secure algorithm allowed for the specified key.
                // .signWith(getKey())
            // .compact();
                

    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwt(String jwtToken) {
        try {
            Jwts.parser().decryptWith(getKey()).build().parse(jwtToken);
            return true;
            // Handling the exceptions that can be thrown by parse()
            /**
             * 1. MalformedJwtException - if the specified JWT was incorrectly constructed 
             * 2. SignatureException - if a JWS signature was discovered, but could not be verified. 
             * 3. SecurityException - if the specified JWT string is a JWE and decryption fails
             * 4. ExpiredJwtException - if the specified JWT is a Claims JWT and the Claims has an expiration time before the time this method is invoked.
             * 5. IllegalArgumentException - if the specified string is null or empty or only whitespace.
             */
        } catch (MalformedJwtException e) {
            logger.error("JWT was incorrectly constructed {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("JWS signature was discovered, but could not be verified {}", e.getMessage());
        } catch (SecurityException e) {
            logger.error("JWT string is a JWE and decryption fails {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token is Expired {}", e);
        } catch (IllegalArgumentException e) {
            logger.error("Token is null or empty or only whitespace {}", e.getMessage());
        }
        return false;

         
    }

    /**
     * @param   jwtToken
     * @Steps   Build - Get claims - get subject (I've set username as subject)
     * @see     #generateTokenByAuth()
     * */
    public String getUsernameFromJwtToken(String jwtToken) {
        return (String) Jwts.parser()
                    // .verifyWith(getKey())
                    .decryptWith(getKey())
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getHeader().get("Subject");
    }
}
