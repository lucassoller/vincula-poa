package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorProcessoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @Mock
    private TentativaContatoRepository tentativaContatoRepository;

    @InjectMocks
    private IndicadorProcessoService indicadorProcessoService;

    private FiltroIndicadorRequestDTO criarFiltro() {

        FiltroIndicadorRequestDTO filtro =
                new FiltroIndicadorRequestDTO();

        filtro.setServicoResponsavelId(1L);
        filtro.setServicoSolicitanteId(2L);
        filtro.setDataInicial(LocalDate.of(2026, 1, 1));
        filtro.setDataFinal(LocalDate.of(2026, 1, 31));

        return filtro;
    }

    @Test
    void deveGerarTodosOsIndicadores() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        when(demandaRepository.countDemandas(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(10L);

        when(demandaRepository.countDemandasFinalizadas(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(5L);

        when(demandaRepository.calcularTempoMedioResolucao(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(2.5);

        when(tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativa(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(1.5);

        when(tentativaContatoRepository.calcularMediaTentativasPorDemanda(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(3.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorServidor(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(4.0);

        var resultado =
                indicadorProcessoService.gerarIndicadores(filtro);

        assertNotNull(resultado);
        assertEquals(5, resultado.size());

        assertEquals(
                "Percentual de demandas resolvidas",
                resultado.get(0).getIndicador()
        );

        assertEquals(
                "Tempo médio para resolução da demanda",
                resultado.get(1).getIndicador()
        );

        assertEquals(
                "Tempo até a primeira tentativa de contato",
                resultado.get(2).getIndicador()
        );

        assertEquals(
                "Média de tentativas de contato por demanda",
                resultado.get(3).getIndicador()
        );

        assertEquals(
                "Média de tentativas de contato por servidor",
                resultado.get(4).getIndicador()
        );
    }

    @Test
    void deveCalcularPercentualDeDemandasResolvidas() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        when(demandaRepository.countDemandas(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(20L);

        when(demandaRepository.countDemandasFinalizadas(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(5L);

        IndicadorValorDTO resultado =
                indicadorProcessoService.percentualDemandasResolvidas(filtro);

        assertEquals(
                "Percentual de demandas resolvidas",
                resultado.getIndicador()
        );

        assertEquals(
                25.0,
                resultado.getValor()
        );
    }

    @Test
    void deveRetornarZeroQuandoNaoHouverDemandas() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        when(demandaRepository.countDemandas(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(0L);

        when(demandaRepository.countDemandasFinalizadas(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(0L);

        IndicadorValorDTO resultado =
                indicadorProcessoService.percentualDemandasResolvidas(filtro);

        assertEquals(
                "Percentual de demandas resolvidas",
                resultado.getIndicador()
        );

        assertEquals(
                0.0,
                resultado.getValor()
        );
    }

    @Test
    void deveCalcularTempoMedioResolucao() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        when(demandaRepository.calcularTempoMedioResolucao(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(3.5);

        IndicadorValorDTO resultado =
                indicadorProcessoService.tempoMedioResolucao(filtro);

        assertEquals(
                "Tempo médio para resolução da demanda",
                resultado.getIndicador()
        );

        assertNotNull(resultado.getValor());

        verify(demandaRepository).calcularTempoMedioResolucao(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );
    }

    @Test
    void deveCalcularTempoMedioAtePrimeiraTentativa() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        when(tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativa(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(2.75);

        IndicadorValorDTO resultado =
                indicadorProcessoService.tempoMedioAtePrimeiraTentativa(filtro);

        assertEquals(
                "Tempo até a primeira tentativa de contato",
                resultado.getIndicador()
        );

        assertNotNull(resultado.getValor());

        verify(tentativaContatoRepository)
                .calcularTempoMedioAtePrimeiraTentativa(
                        1L, 2L,
                        filtro.getDataInicial(),
                        filtro.getDataFinal()
                );
    }

    @Test
    void deveCalcularMediaTentativasPorDemanda() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        when(tentativaContatoRepository.calcularMediaTentativasPorDemanda(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(2.75);

        IndicadorValorDTO resultado =
                indicadorProcessoService.mediaTentativasPorDemanda(filtro);

        assertEquals(
                "Média de tentativas de contato por demanda",
                resultado.getIndicador()
        );

        assertEquals(
                2.75,
                resultado.getValor()
        );

        verify(tentativaContatoRepository)
                .calcularMediaTentativasPorDemanda(
                        1L, 2L,
                        filtro.getDataInicial(),
                        filtro.getDataFinal()
                );
    }

    @Test
    void deveCalcularMediaTentativasPorServidor() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        when(tentativaContatoRepository.calcularMediaTentativasPorServidor(
                1L, 2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(4.25);

        IndicadorValorDTO resultado =
                indicadorProcessoService.mediaTentativasPorServidor(filtro);

        assertEquals(
                "Média de tentativas de contato por servidor",
                resultado.getIndicador()
        );

        assertEquals(
                4.25,
                resultado.getValor()
        );

        verify(tentativaContatoRepository)
                .calcularMediaTentativasPorServidor(
                        1L, 2L,
                        filtro.getDataInicial(),
                        filtro.getDataFinal()
                );
    }
}