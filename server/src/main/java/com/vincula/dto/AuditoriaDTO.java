package com.vincula.dto;

import com.vincula.enums.TipoAcaoAuditoria;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuditoriaDTO {

    private Long id;
    private TipoAcaoAuditoria acao;
    private String entidade;
    private Long entidadeId;
    private String descricao;
    private LocalDateTime dataHora;

    private Long usuarioId;
    private String usuarioNome;
}