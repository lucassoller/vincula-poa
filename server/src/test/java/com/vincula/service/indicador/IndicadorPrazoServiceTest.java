/* package com.vincula.service.indicador;

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
                "0d 1h 1m",
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
        assertEquals("0d 0h 0m", resultado.get(3).getValor());
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
                "0d 0h 0m",
                resultado.get(3).getValor()
        );
    }

    @Test
    void deveListarIndicadoresPorServico() {

        when(demandaRepository.countDentroPrazoPorServico(1L))
                .thenReturn(1L);

        when(demandaRepository.countAtrasadasPorServico(1L))
                .thenReturn(1L);

        when(demandaRepository.countFinalizadasAtrasadasPorServico(1L))
                .thenReturn(1L);

        when(demandaRepository.tempoMedioAtrasoPorServico(1L))
                .thenReturn(10.0);

        service.indicadoresPrazoPorServico(1L);

        verify(demandaRepository)
                .countDentroPrazoPorServico(1L);

        verify(demandaRepository)
                .countAtrasadasPorServico(1L);

        verify(demandaRepository)
                .countFinalizadasAtrasadasPorServico(1L);

        verify(demandaRepository)
                .tempoMedioAtrasoPorServico(1L);
    }

    @Test
    void deveListarIndicadoresPorServidor() {

        when(demandaRepository.countDentroPrazoPorServicoSolicitante(1L))
                .thenReturn(2L);

        when(demandaRepository.countAtrasadasPorServicoSolicitante(1L))
                .thenReturn(1L);

        when(demandaRepository.countFinalizadasAtrasadasPorServicoSolicitante(1L))
                .thenReturn(1L);

        when(demandaRepository.tempoMedioAtrasoPorServicoSolicitante(1L))
                .thenReturn(3600.0);

        List<IndicadorValorDTO> resultado =
                service.indicadoresPrazoPorServidor(1L);

        assertEquals(4, resultado.size());

        verify(demandaRepository)
                .countDentroPrazoPorServicoSolicitante(1L);

        verify(demandaRepository)
                .countAtrasadasPorServicoSolicitante(1L);

        verify(demandaRepository)
                .countFinalizadasAtrasadasPorServicoSolicitante(1L);

        verify(demandaRepository)
                .tempoMedioAtrasoPorServicoSolicitante(1L);
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
    void deveListarIndicadoresPorServicoEPeriodo() {

        Long servicoId = 1L;

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countDentroPrazoPorServicoEPeriodo(servicoId, inicio, fim))
                .thenReturn(2L);

        when(demandaRepository.countDemandasAtrasadasPorServicoEPeriodo(servicoId, inicio, fim))
                .thenReturn(1L);

        when(demandaRepository.countFinalizadasAtrasadasPorServicoEPeriodo(servicoId, inicio, fim))
                .thenReturn(1L);

        when(demandaRepository.tempoMedioAtrasoEmSegundosPorServicoEPeriodo(servicoId, inicio, fim))
                .thenReturn(120.0);

        service.indicadoresPrazoPorServicoEPeriodo(servicoId, inicio, fim);

        verify(demandaRepository)
                .countDentroPrazoPorServicoEPeriodo(servicoId, inicio, fim);

        verify(demandaRepository)
                .countDemandasAtrasadasPorServicoEPeriodo(servicoId, inicio, fim);

        verify(demandaRepository)
                .countFinalizadasAtrasadasPorServicoEPeriodo(servicoId, inicio, fim);

        verify(demandaRepository)
                .tempoMedioAtrasoEmSegundosPorServicoEPeriodo(servicoId, inicio, fim);
    }

    @Test
    void deveListarIndicadoresPorServidorEPeriodo() {

        Long servidorId = 1L;

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countDentroPrazoPorServicoSolicitanteEPeriodo(servidorId, inicio, fim))
                .thenReturn(2L);

        when(demandaRepository.countDemandasAtrasadasPorServicoSolicitanteEPeriodo(servidorId, inicio, fim))
                .thenReturn(1L);

        when(demandaRepository.countFinalizadasAtrasadasPorServicoSolicitanteEPeriodo(servidorId, inicio, fim))
                .thenReturn(1L);

        when(demandaRepository.tempoMedioAtrasoEmSegundosPorServicoSolicitanteEPeriodo(servidorId, inicio, fim))
                .thenReturn(120.0);

        service.indicadoresPrazoPorServidorEPeriodo(servidorId, inicio, fim);

        verify(demandaRepository)
                .countDentroPrazoPorServicoSolicitanteEPeriodo(servidorId, inicio, fim);

        verify(demandaRepository)
                .countDemandasAtrasadasPorServicoSolicitanteEPeriodo(servidorId, inicio, fim);

        verify(demandaRepository)
                .countFinalizadasAtrasadasPorServicoSolicitanteEPeriodo(servidorId, inicio, fim);

        verify(demandaRepository)
                .tempoMedioAtrasoEmSegundosPorServicoSolicitanteEPeriodo(servidorId, inicio, fim);
    }

}

 */