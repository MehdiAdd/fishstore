package com.commerce.backend.model.request.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequestDTO {

    @NotBlank//TODO
    private String username;
    @NotBlank
    private String password;

}
