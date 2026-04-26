package com.vincula.dto.usuario;

import com.vincula.enums.PerfilUsuario;
import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String login;
    private PerfilUsuario perfil;
    private Long unidadeSaudeId;
    private Boolean ativo;
}