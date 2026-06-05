package com.vincula.dto.senha;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RedefinirSenhaDTO {

    private String token;

    @NotBlank(message = "A nova senha é obrigatória")
    @Size(max = 255, message = "A nova senha deve ter no máximo 255 caracteres")
    private String novaSenha;

}
