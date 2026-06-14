package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.repository.DemandaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorPrazoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorPrazoService service;

    @Test
    void deveCalcularIndicadoresPrazo() {

        when(demandaRepository.countDemandasDentroDoPrazo()).thenReturn(5L);
        when(demandaRepository.countDemandasAtrasadas()).thenReturn(3L);
        when(demandaRepository.countDemandasFinalizadasComAtraso()).thenReturn(2L);
        when(demandaRepository.tempoMedioAtrasoEmSegundos()).thenReturn(3661.0);

        List<IndicadorValorDTO> resultado =
                service.indicadoresPrazo();

        assertEquals(4, resultado.size());

        assertEquals(50.0, resultado.get(0).getValor());
        assertEquals(30.0, resultado.get(1).getValor());
        assertEquals(20.0, resultado.get(2).getValor());

        assertEquals(
                "1h 1m 1s",
                resultado.get(3).getValor()
        );
    }

    @Test
    void deveRetornarZeroQuandoNaoExistiremDemandas() {

        when(demandaRepository.countDemandasDentroDoPrazo()).thenReturn(0L);
        when(demandaRepository.countDemandasAtrasadas()).thenReturn(0L);
        when(demandaRepository.countDemandasFinalizadasComAtraso()).thenReturn(0L);
        when(demandaRepository.tempoMedioAtrasoEmSegundos()).thenReturn(0.0);

        List<IndicadorValorDTO> resultado =
                service.indicadoresPrazo();

        assertEquals(0.0, resultado.get(0).getValor());
        assertEquals(0.0, resultado.get(1).getValor());
        assertEquals(0.0, resultado.get(2).getValor());
        assertEquals("0h 0m 0s", resultado.get(3).getValor());
    }

    @Test
    void deveRetornarTempoZeroQuandoTempoForNull() {

        when(demandaRepository.countDemandasDentroDoPrazo()).thenReturn(1L);
        when(demandaRepository.countDemandasAtrasadas()).thenReturn(0L);
        when(demandaRepository.countDemandasFinalizadasComAtraso()).thenReturn(0L);
        when(demandaRepository.tempoMedioAtrasoEmSegundos()).thenReturn(null);

        List<IndicadorValorDTO> resultado =
                service.indicadoresPrazo();

        assertEquals(
                "0h 0m 0s",
                resultado.get(3).getValor()
        );
    }

    @Test
    void deveListarIndicadoresPorUnidade() {

        when(demandaRepository.countDentroPrazoPorUnidade(1L))
                .thenReturn(1L);

        when(demandaRepository.countAtrasadasPorUnidade(1L))
                .thenReturn(1L);

        when(demandaRepository.countFinalizadasAtrasadasPorUnidade(1L))
                .thenReturn(1L);

        when(demandaRepository.tempoMedioAtrasoPorUnidade(1L))
                .thenReturn(10.0);

        service.indicadoresPrazoPorUnidade(1L);

        verify(demandaRepository)
                .countDentroPrazoPorUnidade(1L);

        verify(demandaRepository)
                .countAtrasadasPorUnidade(1L);

        verify(demandaRepository)
                .countFinalizadasAtrasadasPorUnidade(1L);

        verify(demandaRepository)
                .tempoMedioAtrasoPorUnidade(1L);
    }

    @Test
    void deveListarIndicadoresPorServidor() {

        when(demandaRepository.countDentroPrazoPorUnidadeSolicitante(1L))
                .thenReturn(2L);

        when(demandaRepository.countAtrasadasPorUnidadeSolicitante(1L))
                .thenReturn(1L);

        when(demandaRepository.countFinalizadasAtrasadasPorUnidadeSolicitante(1L))
                .thenReturn(1L);

        when(demandaRepository.tempoMedioAtrasoPorUnidadeSolicitante(1L))
                .thenReturn(3600.0);

        List<IndicadorValorDTO> resultado =
                service.indicadoresPrazoPorServidor(1L);

        assertEquals(4, resultado.size());

        verify(demandaRepository)
                .countDentroPrazoPorUnidadeSolicitante(1L);

        verify(demandaRepository)
                .countAtrasadasPorUnidadeSolicitante(1L);

        verify(demandaRepository)
                .countFinalizadasAtrasadasPorUnidadeSolicitante(1L);

        verify(demandaRepository)
                .tempoMedioAtrasoPorUnidadeSolicitante(1L);
    }

    @Test
    void deveListarIndicadoresPorPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countDentroPrazoPorPeriodo(inicio, fim))
                .thenReturn(2L);

        when(demandaRepository.countDemandasAtrasadasPorPeriodo(inicio, fim))
                .thenReturn(1L);

        when(demandaRepository.countFinalizadasAtrasadasPorPeriodo(inicio, fim))
                .thenReturn(1L);

        when(demandaRepository.tempoMedioAtrasoEmSegundosPorPeriodo(inicio, fim))
                .thenReturn(120.0);

        service.indicadoresPrazoPorPeriodo(inicio, fim);

        verify(demandaRepository)
                .countDentroPrazoPorPeriodo(inicio, fim);

        verify(demandaRepository)
                .countDemandasAtrasadasPorPeriodo(inicio, fim);

        verify(demandaRepository)
                .countFinalizadasAtrasadasPorPeriodo(inicio, fim);

        verify(demandaRepository)
                .tempoMedioAtrasoEmSegundosPorPeriodo(inicio, fim);
    }

    @Test
    void deveListarIndicadoresPorUnidadeEPeriodo() {

        Long unidadeId = 1L;

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countDentroPrazoPorUnidadeEPeriodo(unidadeId, inicio, fim))
                .thenReturn(2L);

        when(demandaRepository.countDemandasAtrasadasPorUnidadeEPeriodo(unidadeId, inicio, fim))
                .thenReturn(1L);

        when(demandaRepository.countFinalizadasAtrasadasPorUnidadeEPeriodo(unidadeId, inicio, fim))
                .thenReturn(1L);

        when(demandaRepository.tempoMedioAtrasoEmSegundosPorUnidadeEPeriodo(unidadeId, inicio, fim))
                .thenReturn(120.0);

        service.indicadoresPrazoPorUnidadeEPeriodo(unidadeId, inicio, fim);

        verify(demandaRepository)
                .countDentroPrazoPorUnidadeEPeriodo(unidadeId, inicio, fim);

        verify(demandaRepository)
                .countDemandasAtrasadasPorUnidadeEPeriodo(unidadeId, inicio, fim);

        verify(demandaRepository)
                .countFinalizadasAtrasadasPorUnidadeEPeriodo(unidadeId, inicio, fim);

        verify(demandaRepository)
                .tempoMedioAtrasoEmSegundosPorUnidadeEPeriodo(unidadeId, inicio, fim);
    }

    @Test
    void deveListarIndicadoresPorServidorEPeriodo() {

        Long servidorId = 1L;

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countDentroPrazoPorUnidadeSolicitanteEPeriodo(servidorId, inicio, fim))
                .thenReturn(2L);

        when(demandaRepository.countDemandasAtrasadasPorUnidadeSolicitanteEPeriodo(servidorId, inicio, fim))
                .thenReturn(1L);

        when(demandaRepository.countFinalizadasAtrasadasPorUnidadeSolicitanteEPeriodo(servidorId, inicio, fim))
                .thenReturn(1L);

        when(demandaRepository.tempoMedioAtrasoEmSegundosPorUnidadeSolicitanteEPeriodo(servidorId, inicio, fim))
                .thenReturn(120.0);

        service.indicadoresPrazoPorServidorEPeriodo(servidorId, inicio, fim);

        verify(demandaRepository)
                .countDentroPrazoPorUnidadeSolicitanteEPeriodo(servidorId, inicio, fim);

        verify(demandaRepository)
                .countDemandasAtrasadasPorUnidadeSolicitanteEPeriodo(servidorId, inicio, fim);

        verify(demandaRepository)
                .countFinalizadasAtrasadasPorUnidadeSolicitanteEPeriodo(servidorId, inicio, fim);

        verify(demandaRepository)
                .tempoMedioAtrasoEmSegundosPorUnidadeSolicitanteEPeriodo(servidorId, inicio, fim);
    }

}