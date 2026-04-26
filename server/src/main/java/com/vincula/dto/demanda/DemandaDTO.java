package com.vincula.dto.demanda;

import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.PrazoDemanda;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DemandaDTO {
    @NotNull(message = "Motivo da busca é obrigatório")
    private MotivoBuscaAtiva motivoBuscaAtiva;

    @Size(max = 500, message = "Descrição da busca deve ter no máximo 500 caracteres")
    private String descricaoBusca;

    @NotNull(message = "Prazo pra demanda é obrigatório")
    private PrazoDemanda prazoDemanda;

    @NotNull(message = "Paciente é obrigatório")
    private Long pacienteId;

    @NotNull(message = "Unidade responsável é obrigatória")
    private Long unidadeResponsavelId;

}