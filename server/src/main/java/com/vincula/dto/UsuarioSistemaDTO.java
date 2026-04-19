package com.vincula.dto;

import com.vincula.enums.PerfilUsuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioSistemaDTO {

    private Long id;

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
    @Size(max = 50, message = "Senha deve ter no máximo 50 caracteres")
    private String senha;

    @NotNull(message = "Perfil é obrigatório")
    private PerfilUsuario perfil;

    // Pode ser obrigatório dependendo do perfil (validar no service)
    @Valid
    @NotNull(message = "Unidade de saúde é obrigatório")
    private Long unidadeSaudeId;

    private Boolean ativo;
}