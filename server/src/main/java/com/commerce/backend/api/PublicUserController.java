package com.commerce.backend.api;


import com.commerce.backend.model.dto.TokenResDto;
import com.commerce.backend.model.entity.User;
import com.commerce.backend.model.request.user.*;
import com.commerce.backend.security.JwtUtils;
import com.commerce.backend.security.UserPrincipal;
import com.commerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class PublicUserController extends PublicApiController {

    private final UserService userService;

    @Autowired
    private  AuthenticationManager authenticationManager;


    @Autowired
    public PublicUserController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping(value = "/account/registration")
    public ResponseEntity<HttpStatus> registerUser(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        User user = userService.register(registerUserRequest);
        //tokenService.createEmailConfirmToken(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }





    @PostMapping(value = "/account/signin",  produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> authenticateUser(HttpServletRequest request, @RequestParam String username, @RequestParam String password) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String jwt = JwtUtils.createToken(principal);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.AUTHORIZATION, jwt);
        responseHeaders.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION);

        // Get the subscriber and create a DTO from the principal

        TokenResDto token= TokenResDto.builder().token(jwt).build();
        return new ResponseEntity<>(token, responseHeaders, HttpStatus.OK);
    }
}
