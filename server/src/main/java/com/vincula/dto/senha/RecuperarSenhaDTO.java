package com.vincula.dto.senha;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RecuperarSenhaDTO {
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 50, message = "Email deve ter no máximo 50 caracteres")
    private String email;
}
