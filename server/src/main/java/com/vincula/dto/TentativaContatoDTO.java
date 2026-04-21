package com.vincula.dto;

import com.vincula.enums.TipoTentativaContato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TentativaContatoDTO {

    private Long id;

    @NotNull(message = "Demanda é obrigatória")
    private Long demandaId;

    @NotNull(message = "Tipo é obrigatório")
    @Size(max = 8, message = "Tipo deve ter no máximo 8 caracteres")
    private TipoTentativaContato tipo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;

    private LocalDateTime dataHora;

    private Long usuarioId;
    private String usuarioNome;
}