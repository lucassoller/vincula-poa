package com.vincula.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ObservacaoDTO {

    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;
    
    private LocalDateTime dataHora;

    @NotNull(message = "Paciente é obrigatório")
    private Long pacienteId;
}