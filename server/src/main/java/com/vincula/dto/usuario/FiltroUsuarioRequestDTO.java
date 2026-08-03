package com.vincula.dto.usuario;

import com.vincula.enums.FaixaEtaria;
import lombok.Data;

import java.util.List;

@Data
public class FiltroUsuarioRequestDTO {

    private Long id;
    private String nomeCompleto;
    private List<FaixaEtaria> faixaEtaria;
    private Long unidadeSaudeId;
    private Long unidadeSolicitanteId;
}
