package com.vincula.dto.servidor;

import com.vincula.enums.PerfilServidor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServidorDTO {

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

    @NotBlank(message = "Senha é obrigatória")
    @Size(max = 255, message = "Senha deve ter no máximo 255 caracteres")
    private String senha;

    @NotBlank(message = "Confirmar a senha é obrigatório")
    @Size(max = 255, message = "Senha deve ter no máximo 255 caracteres")
    private String confirmarSenha;

    @NotNull(message = "Perfil é obrigatório")
    private PerfilServidor perfil;

    private Long unidadeSaudeId;

    private Boolean ativo;
}