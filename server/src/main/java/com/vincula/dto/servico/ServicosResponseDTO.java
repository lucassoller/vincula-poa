package com.vincula.dto.servico;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ServicosResponseDTO {
    private List<ServicoShortResponseDTO> todos;
    private List<ServicoShortResponseDTO> ubs;
    private List<ServicoShortResponseDTO> servicos;
    private List<ServicoShortResponseDTO> outros;
    private List<ServicoShortResponseDTO> especializados;
}