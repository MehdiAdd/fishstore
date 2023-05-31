package com.commerce.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class JwtUtils {

    public static String createToken(UserPrincipal principal) {
        
        String token = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("permissions", principal.getPermissions())
                .withClaim("firstName", principal.getFirstName())
                .withClaim("lastName", principal.getLastName())
                .withClaim("gender", principal.getGender())
                .withClaim("locale", principal.getLocale())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()));
        
        return token;
    }
}
