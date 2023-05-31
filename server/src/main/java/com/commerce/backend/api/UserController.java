package com.commerce.backend.api;

import com.commerce.backend.model.request.user.PasswordResetRequest;
import com.commerce.backend.model.request.user.UpdateUserAddressRequest;
import com.commerce.backend.model.request.user.UpdateUserRequest;
import com.commerce.backend.model.response.user.UserResponse;
import com.commerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
public class UserController extends ApiController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/account/all")
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<UserResponse> userResponses = userService.getUsers();
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    @GetMapping(value = "/account/users/delete/{email}")
    public ResponseEntity<List<UserResponse>> deleteUser(@PathVariable String email) {
        userService.deleteUser( email);
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping(value = "/account/users/activate/{email}/{emailVerified}")
    public ResponseEntity<List<UserResponse>> activateUser(@PathVariable Boolean emailVerified,@PathVariable String email) {
         userService.activate(emailVerified,email);
        return new ResponseEntity<>( userService.getUsers(),HttpStatus.OK);
    }

    @GetMapping(value = "/account")
    public ResponseEntity<UserResponse> getUser() {
        UserResponse userResponse = userService.fetchUser();
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PutMapping(value = "/account")
    public ResponseEntity<UserResponse> updateUser(@RequestBody @Valid UpdateUserRequest updateUserRequest) {
        UserResponse userResponse = userService.updateUser(updateUserRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/account/admin-check")
    public ResponseEntity<Boolean> adminCheck() {
        boolean check = userService.checkAdmin();
        return new ResponseEntity<>(check, HttpStatus.OK);
    }

    @PutMapping(value = "/account/address")
    public ResponseEntity<UserResponse> updateUserAddress(@RequestBody @Valid UpdateUserAddressRequest updateUserAddressRequest) {
        UserResponse userResponse = userService.updateUserAddress(updateUserAddressRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/account/password/reset")
    public ResponseEntity<HttpStatus> passwordReset(@RequestBody @Valid PasswordResetRequest passwordResetRequest) {
        userService.resetPassword(passwordResetRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/account/status")
    public ResponseEntity<Boolean> getVerificationStatus() {
        Boolean status = userService.getVerificationStatus();
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
