package com.vincula.dto.auditoria;

import com.vincula.enums.TipoAcaoAuditoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AuditoriaDTO {

    private Long id;
    private TipoAcaoAuditoria acao;
    private String entidade;
    private Long entidadeId;
    private String descricao;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime dataHora;

    private Long servidorId;
    private String servidorNome;

    private String ip;
}