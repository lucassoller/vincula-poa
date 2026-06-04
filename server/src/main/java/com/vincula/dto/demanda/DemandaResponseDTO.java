package com.vincula.dto.demanda;

import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.PrazoDemanda;
import com.vincula.enums.StatusDemanda;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class DemandaResponseDTO {

    private Long id;

    private MotivoBuscaAtiva motivoBuscaAtiva;
    private String descricaoBusca;

    private PrazoDemanda prazoDemanda;
    private StatusDemanda status;

    private DesfechoDemanda desfecho;
    private String descricaoDesfecho;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataHoraCriacao;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataHoraLimite;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataHoraFinalizacao;

    private Long usuarioId;
    private String usuarioNome;

    private Long unidadeSolicitanteId;
    private String unidadeSolicitanteNome;

    private Long unidadeResponsavelId;
    private String unidadeResponsavelNome;

    private Long servidorCriadorId;
    private String servidorCriadorNome;

    private Long servidorEncerramentoId;
    private String servidorEncerramentoNome;

    private Long unidadeResponsavelAnteriorId;
    private String unidadeResponsavelAnteriorNome;

    private Boolean foiRedirecionada;
    private String motivoRedirecionamento;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataHoraRedirecionamento;

    private Long servidorRedirecionamentoId;
    private String servidorRedirecionamentoNome;
}