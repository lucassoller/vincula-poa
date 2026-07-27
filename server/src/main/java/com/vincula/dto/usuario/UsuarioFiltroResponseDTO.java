package com.vincula.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioFiltroResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String documento;

}