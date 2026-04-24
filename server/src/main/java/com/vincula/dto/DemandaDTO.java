package com.vincula.dto;

import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.PrazoDemanda;
import com.vincula.enums.StatusDemanda;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DemandaDTO {

    private Long id;

    @NotNull(message = "Motivo da busca é obrigatório")
    private MotivoBuscaAtiva motivoBuscaAtiva;

    @Size(max = 500, message = "Descrição da busca deve ter no máximo 500 caracteres")
    private String descricaoBusca;

    @NotNull(message = "Prazo pra demanda é obrigatório")
    private PrazoDemanda prazoDemanda;

    private StatusDemanda status;

    private DesfechoDemanda desfecho;

    @Size(max = 500, message = "Descrição do desfecho da demanda deve ter no máximo 500 caracteres")
    private String descricaoDesfecho;

    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraFinalizacao;

    @NotNull(message = "Paciente é obrigatório")
    private Long pacienteId;

    private Long unidadeSolicitanteId;

    @NotNull(message = "Unidade responsável é obrigatória")
    private Long unidadeResponsavelId;

    private Long usuarioCriadorId;
    private String usuarioCriadorNome;

    private Long usuarioEncerramentoId;
    private String usuarioEncerramentoNome;

    private Boolean foiRedirecionada;
    private Long unidadeResponsavelAnteriorId;

    @Size(max = 500, message = "Motivo do redirecionamento da demanda deve ter no máximo 500 caracteres")
    private String motivoRedirecionamento;
    private LocalDateTime dataHoraRedirecionamento;

    private Long usuarioRedirecionamentoId;
    private String usuarioRedirecionamentoNome;
}