package com.vincula.dto.indicador;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FiltroIndicadorRequestDTO {
    private Long unidadeResponsavelId;

    private Long unidadeSolicitanteId;

    private LocalDate dataInicial;

    private LocalDate dataFinal;
}
