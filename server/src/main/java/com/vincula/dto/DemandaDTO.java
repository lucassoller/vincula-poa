package com.vincula.dto;

import com.vincula.enums.StatusDemanda;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DemandaDTO {

    private Long id;

    @NotNull(message = "Paciente é obrigatório")
    private Long pacienteId;

    @NotNull(message = "Unidade de saúde é obrigatória")
    private Long unidadeSaudeId;

    @NotBlank(message = "Motivo é obrigatório")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String motivo;

    @Size(max = 12, message = "Status da demanda deve ter no máximo 12 caracteres")
    private StatusDemanda status;

    private LocalDateTime dataHoraCriacao;

    private LocalDateTime dataHoraFinalizacao;

    private Long usuarioCriadorId;

    private String usuarioCriadorNome;
}