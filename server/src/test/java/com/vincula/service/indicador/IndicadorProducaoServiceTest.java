package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.dto.projection.StatusQuantidadeProjection;
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
class IndicadorProducaoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorProducaoService service;

    @Test
    void deveTraduzirStatusAberta() {

        StatusQuantidadeProjection item =
                mock(StatusQuantidadeProjection.class);

        when(item.getStatus()).thenReturn("ABERTA");
        when(item.getQuantidade()).thenReturn(5.0);

        when(demandaRepository.agruparPorStatus())
                .thenReturn(List.of(item));

        when(demandaRepository.countBy())
                .thenReturn(5.0);

        List<IndicadorValorDTO> resultado =
                service.indicadoresGerais();

        assertEquals(
                "Demandas abertas",
                resultado.get(0).getIndicador()
        );
    }

    @Test
    void deveTraduzirStatusEmAndamento() {

        StatusQuantidadeProjection item =
                mock(StatusQuantidadeProjection.class);

        when(item.getStatus()).thenReturn("EM_ANDAMENTO");
        when(item.getQuantidade()).thenReturn(5.0);

        when(demandaRepository.agruparPorStatus())
                .thenReturn(List.of(item));

        when(demandaRepository.countBy())
                .thenReturn(5.0);

        List<IndicadorValorDTO> resultado =
                service.indicadoresGerais();

        assertEquals(
                "Demandas em andamento",
                resultado.get(0).getIndicador()
        );
    }

    @Test
    void deveTraduzirStatusFinalizada() {

        StatusQuantidadeProjection item =
                mock(StatusQuantidadeProjection.class);

        when(item.getStatus()).thenReturn("FINALIZADA");
        when(item.getQuantidade()).thenReturn(5.0);

        when(demandaRepository.agruparPorStatus())
                .thenReturn(List.of(item));

        when(demandaRepository.countBy())
                .thenReturn(5.0);

        List<IndicadorValorDTO> resultado =
                service.indicadoresGerais();

        assertEquals(
                "Demandas finalizadas",
                resultado.get(0).getIndicador()
        );
    }

    @Test
    void deveRetornarStatusOriginalQuandoNaoTraduzido() {

        StatusQuantidadeProjection item =
                mock(StatusQuantidadeProjection.class);

        when(item.getStatus()).thenReturn("QUALQUER_COISA");
        when(item.getQuantidade()).thenReturn(5.0);

        when(demandaRepository.agruparPorStatus())
                .thenReturn(List.of(item));

        when(demandaRepository.countBy())
                .thenReturn(5.0);

        List<IndicadorValorDTO> resultado =
                service.indicadoresGerais();

        assertEquals(
                "QUALQUER_COISA",
                resultado.get(0).getIndicador()
        );
    }

    @Test
    void deveListarIndicadoresPorUnidade() {

        when(demandaRepository.agruparPorStatusPorUnidade(1L))
                .thenReturn(List.of());

        when(demandaRepository.countByUnidadeResponsavelId(1L))
                .thenReturn(10.0);

        List<IndicadorValorDTO> resultado =
                service.indicadoresPorUnidade(1L);

        assertEquals(1, resultado.size());

        verify(demandaRepository)
                .agruparPorStatusPorUnidade(1L);

        verify(demandaRepository)
                .countByUnidadeResponsavelId(1L);
    }

    @Test
    void deveListarIndicadoresPorUnidadeSolicitante() {

        when(demandaRepository.agruparPorStatusPorUnidadeSolicitante(1L))
                .thenReturn(List.of());

        when(demandaRepository.countByUnidadeSolicitanteId(1L))
                .thenReturn(10.0);

        service.indicadoresPorUnidadeSolicitante(1L);

        verify(demandaRepository)
                .agruparPorStatusPorUnidadeSolicitante(1L);

        verify(demandaRepository)
                .countByUnidadeSolicitanteId(1L);
    }

    @Test
    void deveListarIndicadoresPorPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.agruparPorStatusPorPeriodo(inicio, fim))
                .thenReturn(List.of());

        when(demandaRepository.countByDataHoraCriacaoBetween(inicio, fim))
                .thenReturn(5.0);

        when(demandaRepository.countByDataHoraFinalizacaoBetween(inicio, fim))
                .thenReturn(3.0);

        List<IndicadorValorDTO> resultado =
                service.indicadoresPorPeriodo(inicio, fim);

        assertEquals(2, resultado.size());
    }

    @Test
    void deveListarIndicadoresPorUnidadeEPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.agruparPorStatusPorUnidadeEPeriodo(1L, inicio, fim))
                .thenReturn(List.of());

        when(demandaRepository.countByUnidadeResponsavelIdAndDataHoraCriacaoBetween(1L, inicio, fim))
                .thenReturn(5.0);

        when(demandaRepository.countByUnidadeResponsavelIdAndDataHoraFinalizacaoBetween(1L, inicio, fim))
                .thenReturn(2.0);

        service.indicadoresPorUnidadeEPeriodo(1L, inicio, fim);

        verify(demandaRepository)
                .agruparPorStatusPorUnidadeEPeriodo(1L, inicio, fim);
    }

    @Test
    void deveListarIndicadoresPorUnidadeSolicitanteEPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.agruparPorStatusPorUnidadeSolicitanteEPeriodo(1L, inicio, fim))
                .thenReturn(List.of());

        when(demandaRepository.countByUnidadeSolicitanteIdAndDataHoraCriacaoBetween(1L, inicio, fim))
                .thenReturn(5.0);

        when(demandaRepository.countByUnidadeSolicitanteIdAndDataHoraFinalizacaoBetween(1L, inicio, fim))
                .thenReturn(2.0);

        service.indicadoresPorServidorEPeriodo(1L, inicio, fim);

        verify(demandaRepository)
                .agruparPorStatusPorUnidadeSolicitanteEPeriodo(1L, inicio, fim);
    }
}