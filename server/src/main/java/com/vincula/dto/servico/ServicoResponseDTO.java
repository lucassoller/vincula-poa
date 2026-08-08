package com.vincula.dto.servico;

import com.vincula.dto.endereco.EnderecoResponseDTO;
import com.vincula.enums.TipoServico;
import lombok.Data;

@Data
public class ServicoResponseDTO {

    private Long id;
    private String nome;
    private String cnes;
    private String telefone;
    private String telefone2;
    private TipoServico tipoServico;
    private EnderecoResponseDTO endereco;
}