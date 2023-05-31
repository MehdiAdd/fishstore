package com.commerce.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.commerce.backend.model.entity.User;
import com.fasterxml.jackson.databind.node.TextNode;

import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Read the Authorization header, where the JWT token should be
        String header = request.getHeader(JwtProperties.HEADER_STRING);

        // If header does not contain BEARER or is null delegate to Spring impl and exit
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        // If header is present, try grab user principal from database and perform authorization
        Authentication authentication = getUsernamePasswordAuthentication(request, response);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue filter execution
        chain.doFilter(request, response);
    }

    private Authentication getUsernamePasswordAuthentication(HttpServletRequest request, HttpServletResponse response) {
        
        String token = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX,"");
        if (token != null) {
            try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()))
                    .build()
                    .verify(token);
            
            User user = parseSubscriber(jwt);
            UserPrincipal principal = new UserPrincipal(user);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            return auth;
        } catch (AlgorithmMismatchException 
                | SignatureVerificationException 
                | TokenExpiredException
                | InvalidClaimException e) {
            logger.error("token exception occurs", e);
            request.setAttribute("tokenException", e.getMessage());
        } catch (JWTVerificationException e) {
            logger.error("token exception occurs", e);
            request.setAttribute("tokenException", e.getMessage());
        }
            // skip check in DB in every call
            // Search in the DB if we find the user by token subject (username)
            // If so, then grab user details and create spring auth token using username, pass, authorities/roles
            //            if (userName != null) {
            //    Subscriber subscriber = subscriberService.findByUsername(userName);
            //    UserPrincipal principal = new UserPrincipal(subscriber);
            //    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userName, null, principal.getAuthorities());
            //    return auth;
            //}
            //return null;
        }
        return null;
    }
    
    private User parseSubscriber(DecodedJWT jwt) {
        
        User user = new User();
        user.setFirstName(getClaimAstext(jwt, "firstName"));
        user.setLastName(getClaimAstext(jwt, "lastName"));
        //user.setGender(getClaimAstext(jwt, "gender"));
        user.setPermissions(getClaimAstext(jwt, "permissions"));
       // user.setLocale(getClaimAstext(jwt, "locale"));
        user.setEmail(jwt.getSubject());
        
        return user;
    }
    
    private String getClaimAstext(DecodedJWT jwt, String name) {

        Claim claim = jwt.getClaim(name);
        if (claim.isNull()) {
            return Strings.EMPTY;
        } else {
            return claim.as(TextNode.class).asText();
        }
    }
}
