package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorRankingDTO;
import com.vincula.dto.projection.RankingQuantidadeProjection;
import com.vincula.dto.projection.RankingValorProjection;
import com.vincula.entity.Servidor;
import com.vincula.enums.PerfilServidor;
import com.vincula.exception.BusinessException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import com.vincula.service.ServidorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndicadorRankingServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @Mock
    private ServidorService servidorService;

    @Mock
    private TentativaContatoRepository tentativaContatoRepository;

    @InjectMocks
    private IndicadorRankingService indicadorRankingService;

    @Test
    void deveLancarExcecaoQuandoServidorNaoForGestaoMunicipal() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.SERVIDOR_APS);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertThrows(
                BusinessException.class,
                () -> indicadorRankingService.rankingPorTotalDemandas()
        );
    }

    @Test
    void deveRetornarRankingPorTotalDemandas() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        RankingQuantidadeProjection ranking =
                mock(RankingQuantidadeProjection.class);

        when(ranking.getUnidadeSaudeId()).thenReturn(1L);
        when(ranking.getUnidadeSaudeNome()).thenReturn("UBS Centro");
        when(ranking.getValor()).thenReturn(10L);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.rankingUnidadesPorTotalDemandas())
                .thenReturn(List.of(ranking));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.rankingPorTotalDemandas();

        assertEquals(1, resultado.size());
        assertEquals(10.0, resultado.get(0).getValor());
    }

    @Test
    void deveRetornarRankingPorPercentualResolucao() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        RankingValorProjection ranking =
                mock(RankingValorProjection.class);

        when(ranking.getUnidadeSaudeId()).thenReturn(1L);
        when(ranking.getUnidadeSaudeNome()).thenReturn("UBS Centro");
        when(ranking.getValor()).thenReturn(83.456);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.rankingUnidadesPorPercentualResolucao())
                .thenReturn(List.of(ranking));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.rankingPorPercentualResolucao();

        assertEquals(83.46, resultado.get(0).getValor());
    }

    @Test
    void deveRetornarRankingPorTempoMedioResolucao() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        RankingValorProjection ranking =
                mock(RankingValorProjection.class);

        when(ranking.getUnidadeSaudeId()).thenReturn(1L);
        when(ranking.getUnidadeSaudeNome()).thenReturn("UBS Centro");
        when(ranking.getValor()).thenReturn(3661.0);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.rankingUnidadesPorTempoMedioResolucao())
                .thenReturn(List.of(ranking));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.rankingPorTempoMedioResolucao();

        assertEquals("0d 1h 1m", resultado.get(0).getValor());
    }

    @Test
    void deveRetornarRankingPorTempoAtePrimeiraTentativa() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        RankingValorProjection ranking =
                mock(RankingValorProjection.class);

        when(ranking.getUnidadeSaudeId()).thenReturn(1L);
        when(ranking.getUnidadeSaudeNome()).thenReturn("UBS Centro");
        when(ranking.getValor()).thenReturn(7200.0);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(tentativaContatoRepository.rankingUnidadesPorTempoAtePrimeiraTentativa())
                .thenReturn(List.of(ranking));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.rankingPorTempoAtePrimeiraTentativa();

        assertEquals("0d 2h 0m", resultado.get(0).getValor());
    }

    @Test
    void deveRetornarZeroQuandoValorRankingQuantidadeForNull() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        RankingQuantidadeProjection ranking =
                mock(RankingQuantidadeProjection.class);

        when(ranking.getUnidadeSaudeId()).thenReturn(1L);
        when(ranking.getUnidadeSaudeNome()).thenReturn("UBS");
        when(ranking.getValor()).thenReturn(null);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.rankingUnidadesPorTotalDemandas())
                .thenReturn(List.of(ranking));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.rankingPorTotalDemandas();

        assertEquals(0.0, resultado.get(0).getValor());
    }

    @Test
    void deveRetornarTempoZeroQuandoValorForNull() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        RankingValorProjection ranking = mock(RankingValorProjection.class);

        when(ranking.getUnidadeSaudeId()).thenReturn(1L);
        when(ranking.getUnidadeSaudeNome()).thenReturn("UBS Centro");
        when(ranking.getValor()).thenReturn(null);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.rankingUnidadesPorTempoMedioResolucao())
                .thenReturn(List.of(ranking));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.rankingPorTempoMedioResolucao();

        assertEquals("0d 0h 0m", resultado.get(0).getValor());
    }

    @Test
    void deveRetornarTempoZeroQuandoValorForZero() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        RankingValorProjection ranking = mock(RankingValorProjection.class);

        when(ranking.getUnidadeSaudeId()).thenReturn(1L);
        when(ranking.getUnidadeSaudeNome()).thenReturn("UBS Centro");
        when(ranking.getValor()).thenReturn(0.0);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.rankingUnidadesPorTempoMedioResolucao())
                .thenReturn(List.of(ranking));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.rankingPorTempoMedioResolucao();

        assertEquals("0d 0h 0m", resultado.get(0).getValor());
    }

    @Test
    void deveRetornarZeroQuandoValorRankingPercentualForNull() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        RankingValorProjection ranking = mock(RankingValorProjection.class);

        when(ranking.getUnidadeSaudeId()).thenReturn(1L);
        when(ranking.getUnidadeSaudeNome()).thenReturn("UBS Centro");
        when(ranking.getValor()).thenReturn(null);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.rankingUnidadesPorPercentualResolucao())
                .thenReturn(List.of(ranking));

        List<IndicadorRankingDTO> resultado =
                indicadorRankingService.rankingPorPercentualResolucao();

        assertEquals(0.0, resultado.get(0).getValor());
    }

}