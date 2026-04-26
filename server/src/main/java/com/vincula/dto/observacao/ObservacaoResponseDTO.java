package com.vincula.dto.observacao;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ObservacaoResponseDTO {

    private Long id;
    private String descricao;
    private LocalDateTime dataHora;
    private Long pacienteId;
    private Long usuarioId;
    private String usuarioNome;
}