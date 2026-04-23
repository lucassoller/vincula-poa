package com.vincula.dto;

import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.Usuario;
import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.PrazoDemanda;
import com.vincula.enums.StatusDemanda;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DemandaDTO {

    private Long id;

    @NotNull(message = "Motivo da busca é obrigatório")
    @Size(max = 14, message = "Motivo da busca deve ter no máximo 14 caracteres")
    private MotivoBuscaAtiva motivoBuscaAtiva;

    @Size(max = 500, message = "Descrição da busca deve ter no máximo 500 caracteres")
    private String descricaoBusca;

    @NotNull(message = "Prazo pra demanda é obrigatório")
    private PrazoDemanda prazoDemanda;

    @Size(max = 12, message = "Status da demanda deve ter no máximo 12 caracteres")
    private StatusDemanda status;

    @Size(max = 20, message = "Desfecho da demanda deve ter no máximo 20 caracteres")
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
}