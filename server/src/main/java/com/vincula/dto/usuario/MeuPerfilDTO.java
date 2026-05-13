package com.vincula.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MeuPerfilDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 50, message = "Email deve ter no máximo 50 caracteres")
    private String email;

    @NotBlank(message = "Login é obrigatório")
    @Size(max = 50, message = "Login deve ter no máximo 50 caracteres")
    private String login;
}
