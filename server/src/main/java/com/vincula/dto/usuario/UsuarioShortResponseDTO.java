package com.vincula.dto.usuario;

import lombok.Data;

@Data
public class UsuarioShortResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String documento;
    private String servicoNome;
    private Long servicoId;
    private Long servicoSolicitanteId;
    private String servicoSolicitanteNome;

}