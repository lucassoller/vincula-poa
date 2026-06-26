package com.vincula.dto.demanda;

import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.MotivoComplemento;
import com.vincula.enums.PrazoDemanda;
import com.vincula.enums.Prioridade;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DemandaDTO {
    @NotNull(message = "O motivo da busca é obrigatório")
    private MotivoBuscaAtiva motivoBuscaAtiva;

    private MotivoComplemento motivoComplemento;
    
    @NotNull(message = "A prioridade é obrigatória")
    private Prioridade prioridade;

    @Size(max = 500, message = "A descrição da busca deve ter no máximo 500 caracteres")
    private String descricaoBusca;

    @NotNull(message = "O prazo da demanda é obrigatório")
    private PrazoDemanda prazoDemanda;

    @NotNull(message = "O usuário é obrigatório")
    private Long usuarioId;
}