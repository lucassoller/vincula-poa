package com.vincula.dto.demanda;

import com.vincula.enums.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FiltroDemandaRequestDTO {

    private List<StatusDemanda> status;

    private List<Prioridade> prioridade;

    private List<PrazoAtual> tempo;

    private MotivoBuscaAtiva motivo;

    private MotivoComplemento complemento;

    private Long unidadeResponsavelId;

    private Long unidadeSolicitanteId;

    private LocalDate dataAbInicial;

    private LocalDate dataAbFinal;

    private LocalDate dataEnInicial;

    private LocalDate dataEnFinal;
}
