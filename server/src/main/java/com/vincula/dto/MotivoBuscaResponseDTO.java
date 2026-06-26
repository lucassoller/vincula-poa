package com.vincula.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MotivoBuscaResponseDTO {

    private String valor;
    private String descricao;
    private List<MotivoComplementoResponseDTO> complementos;

}