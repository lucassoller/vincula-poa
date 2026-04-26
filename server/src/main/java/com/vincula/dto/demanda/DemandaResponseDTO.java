package com.vincula.dto.demanda;

import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.PrazoDemanda;
import com.vincula.enums.StatusDemanda;
import lombok.Data;

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
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraLimite;
    private LocalDateTime dataHoraFinalizacao;
    private Long pacienteId;
    private Long unidadeSolicitanteId;
    private Long unidadeResponsavelId;
    private Long usuarioCriadorId;
    private String usuarioCriadorNome;
    private Long usuarioEncerramentoId;
    private String usuarioEncerramentoNome;
    private Boolean foiRedirecionada;
    private Long unidadeResponsavelAnteriorId;
    private String motivoRedirecionamento;
    private LocalDateTime dataHoraRedirecionamento;
    private Long usuarioRedirecionamentoId;
    private String usuarioRedirecionamentoNome;
}