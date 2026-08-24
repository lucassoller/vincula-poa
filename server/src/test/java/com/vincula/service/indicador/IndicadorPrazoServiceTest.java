package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.repository.DemandaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorPrazoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorPrazoService indicadorPrazoService;

    @Test
    void deveGerarIndicadoresDePrazo() {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();
        filtro.setServicoResponsavelId(1L);
        filtro.setServicoSolicitanteId(2L);
        filtro.setDataInicial(LocalDate.of(2026, 1, 1));
        filtro.setDataFinal(LocalDate.of(2026, 1, 31));

        when(demandaRepository.countDemandasDentroDoPrazo(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(70L);

        when(demandaRepository.countDemandasAtrasadas(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(20L);

        when(demandaRepository.countDemandasFinalizadasComAtraso(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(10L);

        when(demandaRepository.calcularTempoMedioAtraso(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(2.5);

        List<IndicadorValorDTO> resultado =
                indicadorPrazoService.gerarIndicadores(filtro);

        assertNotNull(resultado);
        assertEquals(4, resultado.size());

        assertEquals("Demandas dentro do prazo",
                resultado.get(0).getIndicador());

        assertEquals("Demandas atrasadas",
                resultado.get(1).getIndicador());

        assertEquals("Demandas finalizadas com atraso",
                resultado.get(2).getIndicador());

        assertEquals("Tempo médio de atraso",
                resultado.get(3).getIndicador());

        verify(demandaRepository).countDemandasDentroDoPrazo(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        verify(demandaRepository).countDemandasAtrasadas(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        verify(demandaRepository).countDemandasFinalizadasComAtraso(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        verify(demandaRepository).calcularTempoMedioAtraso(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );
    }

    @Test
    void deveRetornarZeroQuandoNaoHouverDemandas() {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();

        filtro.setServicoResponsavelId(1L);
        filtro.setServicoSolicitanteId(2L);
        filtro.setDataInicial(LocalDate.of(2026, 1, 1));
        filtro.setDataFinal(LocalDate.of(2026, 1, 31));

        when(demandaRepository.countDemandasDentroDoPrazo(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(0L);

        when(demandaRepository.countDemandasAtrasadas(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(0L);

        when(demandaRepository.countDemandasFinalizadasComAtraso(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(0L);

        when(demandaRepository.calcularTempoMedioAtraso(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(0.0);

        List<IndicadorValorDTO> resultado =
                indicadorPrazoService.gerarIndicadores(filtro);

        assertEquals(4, resultado.size());

        assertEquals(0.0, resultado.get(0).getValor());
        assertEquals(0.0, resultado.get(1).getValor());
        assertEquals(0.0, resultado.get(2).getValor());

        verify(demandaRepository).countDemandasDentroDoPrazo(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        verify(demandaRepository).countDemandasAtrasadas(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        verify(demandaRepository).countDemandasFinalizadasComAtraso(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        verify(demandaRepository).calcularTempoMedioAtraso(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );
    }
}