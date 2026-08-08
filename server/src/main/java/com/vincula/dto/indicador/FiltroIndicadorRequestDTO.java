package com.vincula.dto.indicador;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FiltroIndicadorRequestDTO {
    private Long servicoResponsavelId;

    private Long servicoSolicitanteId;

    private LocalDate dataInicial;

    private LocalDate dataFinal;
}
