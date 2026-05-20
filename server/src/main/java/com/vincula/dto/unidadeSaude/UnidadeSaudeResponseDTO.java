package com.vincula.dto.unidadeSaude;

import com.vincula.dto.endereco.EnderecoResponseDTO;
import lombok.Data;

@Data
public class UnidadeSaudeResponseDTO {

    private Long id;
    private String nome;
    private String cnes;
    private String telefone;
    private String telefone2;
    private EnderecoResponseDTO endereco;
}