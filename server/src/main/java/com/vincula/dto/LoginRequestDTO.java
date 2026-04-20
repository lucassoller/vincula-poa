package com.vincula.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "Login é obrigatório")
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}