package com.vincula.dto.usuario;

import lombok.Data;

@Data
public class UsuarioShortResponseDTO {

    private Long id;
    private String nome;
    private String email;
}