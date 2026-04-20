package com.vincula.dto;

import com.vincula.enums.PerfilUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private Long id;
    private String nome;
    private String login;
    private PerfilUsuario perfil;
    private Boolean ativo;
}