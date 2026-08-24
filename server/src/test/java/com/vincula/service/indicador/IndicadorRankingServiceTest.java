package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorRankingDTO;
import com.vincula.dto.projection.RankingQuantidadeProjection;
import com.vincula.dto.projection.RankingValorProjection;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorRankingServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @Mock
    private TentativaContatoRepository tentativaContatoRepository;

    @Mock
    private RankingQuantidadeProjection quantidadeProjection;

    @Mock
    private RankingValorProjection valorProjection;

    @InjectMocks
    private IndicadorRankingService indicadorRankingService;


    // =========================================================
    // Ranking por total de demandas
    // =========================================================

    @Test
    void deveGerarRankingPorTotalDemandas() {

        when(quantidadeProjection.getServicoId())
                .thenReturn(1L);

        when(quantidadeProjection.getServicoNome())
                .thenReturn("UBS Centro");

        when(quantidadeProjection.getValor())
                .thenReturn(15L);

        when(demandaRepository.rankingServicosPorTotalDemandas())
                .thenReturn(List.of(quantidadeProjection));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.gerarRankingPorTotalDemandas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        IndicadorRankingDTO dto = resultado.get(0);

        assertEquals(1L, dto.getServicoId());
        assertEquals("UBS Centro", dto.getServicoNome());
        assertEquals(15.0, dto.getValor());

        verify(demandaRepository)
                .rankingServicosPorTotalDemandas();
    }


    @Test
    void deveConsiderarZeroQuandoValorDoRankingForNulo() {

        when(quantidadeProjection.getServicoId())
                .thenReturn(1L);

        when(quantidadeProjection.getServicoNome())
                .thenReturn("UBS Centro");

        when(quantidadeProjection.getValor())
                .thenReturn(null);

        when(demandaRepository.rankingServicosPorTotalDemandas())
                .thenReturn(List.of(quantidadeProjection));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.gerarRankingPorTotalDemandas();

        assertEquals(1, resultado.size());

        assertEquals(
                0.0,
                resultado.get(0).getValor()
        );
    }


    @Test
    void deveRetornarListaVaziaQuandoNaoHouverDemandas() {

        when(demandaRepository.rankingServicosPorTotalDemandas())
                .thenReturn(List.of());

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.gerarRankingPorTotalDemandas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(demandaRepository)
                .rankingServicosPorTotalDemandas();
    }


    // =========================================================
    // Ranking por percentual de resolução
    // =========================================================

    @Test
    void deveGerarRankingPorPercentualResolucao() {

        when(valorProjection.getServicoId())
                .thenReturn(1L);

        when(valorProjection.getServicoNome())
                .thenReturn("UBS Centro");

        when(valorProjection.getValor())
                .thenReturn(87.456);

        when(demandaRepository.rankingServicosPorPercentualResolucao())
                .thenReturn(List.of(valorProjection));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.gerarRankingPorPercentualResolucao();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        IndicadorRankingDTO dto = resultado.get(0);

        assertEquals(1L, dto.getServicoId());
        assertEquals("UBS Centro", dto.getServicoNome());

        // Ajuste se o seu arredondar() usar outra quantidade de casas.
        assertEquals(87.46, dto.getValor());

        verify(demandaRepository)
                .rankingServicosPorPercentualResolucao();
    }


    @Test
    void deveRetornarListaVaziaNoRankingPorPercentual() {

        when(demandaRepository.rankingServicosPorPercentualResolucao())
                .thenReturn(List.of());

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.gerarRankingPorPercentualResolucao();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(demandaRepository)
                .rankingServicosPorPercentualResolucao();
    }


    // =========================================================
    // Ranking por tempo médio de resolução
    // =========================================================

    @Test
    void deveGerarRankingPorTempoMedioResolucao() {

        when(valorProjection.getServicoId())
                .thenReturn(1L);

        when(valorProjection.getServicoNome())
                .thenReturn("UBS Centro");

        when(valorProjection.getValor())
                .thenReturn(5.5);

        when(demandaRepository.rankingServicosPorTempoMedioResolucao())
                .thenReturn(List.of(valorProjection));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.gerarRankingPorTempoMedioResolucao();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        IndicadorRankingDTO dto = resultado.get(0);

        assertEquals(1L, dto.getServicoId());
        assertEquals("UBS Centro", dto.getServicoNome());

        assertNotNull(dto.getValor());

        verify(demandaRepository)
                .rankingServicosPorTempoMedioResolucao();
    }


    @Test
    void deveRetornarListaVaziaNoRankingPorTempoMedio() {

        when(demandaRepository.rankingServicosPorTempoMedioResolucao())
                .thenReturn(List.of());

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.gerarRankingPorTempoMedioResolucao();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(demandaRepository)
                .rankingServicosPorTempoMedioResolucao();
    }


    // =========================================================
    // Ranking por tempo até primeira tentativa
    // =========================================================

    @Test
    void deveGerarRankingPorTempoAtePrimeiraTentativa() {

        when(valorProjection.getServicoId())
                .thenReturn(1L);

        when(valorProjection.getServicoNome())
                .thenReturn("UBS Centro");

        when(valorProjection.getValor())
                .thenReturn(2.75);

        when(tentativaContatoRepository
                .rankingServicosPorTempoAtePrimeiraTentativa())
                .thenReturn(List.of(valorProjection));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService
                        .gerarRankingPorTempoAtePrimeiraTentativa();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        IndicadorRankingDTO dto = resultado.get(0);

        assertEquals(1L, dto.getServicoId());
        assertEquals("UBS Centro", dto.getServicoNome());
        assertNotNull(dto.getValor());

        verify(tentativaContatoRepository)
                .rankingServicosPorTempoAtePrimeiraTentativa();
    }


    @Test
    void deveRetornarListaVaziaNoRankingPorTempoAtePrimeiraTentativa() {

        when(tentativaContatoRepository
                .rankingServicosPorTempoAtePrimeiraTentativa())
                .thenReturn(List.of());

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService
                        .gerarRankingPorTempoAtePrimeiraTentativa();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(tentativaContatoRepository)
                .rankingServicosPorTempoAtePrimeiraTentativa();
    }
}