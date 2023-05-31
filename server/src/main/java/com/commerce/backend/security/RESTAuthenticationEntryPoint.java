package com.commerce.backend.security;

import com.commerce.backend.error.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RESTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        final String tokenException = (String) request.getAttribute("tokenException");
        if (tokenException != null) {
            List<String> details = new ArrayList<String>();
            details.add(tokenException);
            ApiError err = new ApiError( HttpStatus.UNAUTHORIZED, "unauthorized exception "+details.toString(), null);
            String json = objectMapper.writeValueAsString(err);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println(json);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
