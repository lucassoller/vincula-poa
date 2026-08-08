/*package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.enums.StatusDemanda;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndicadorProcessoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @Mock
    private TentativaContatoRepository tentativaContatoRepository;

    @InjectMocks
    private IndicadorProcessoService indicadorProcessoService;

    @Test
    void deveRetornarPercentualZeroQuandoNaoExistemDemandas() {
        when(demandaRepository.countBy()).thenReturn(0.0);
        when(demandaRepository.countByStatus(StatusDemanda.FINALIZADA))
                .thenReturn(0.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidas();

        assertEquals(0.0, dto.getValor());
    }

    @Test
    void deveCalcularPercentualDemandasResolvidas() {
        when(demandaRepository.countBy()).thenReturn(10.0);
        when(demandaRepository.countByStatus(StatusDemanda.FINALIZADA))
                .thenReturn(4.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidas();

        assertEquals(40.0, dto.getValor());
    }

    @Test
    void deveMontarProcessoGeral() {
        when(demandaRepository.countBy()).thenReturn(10.0);
        when(demandaRepository.countByStatus(StatusDemanda.FINALIZADA))
                .thenReturn(5.0);

        when(demandaRepository.calcularTempoMedioResolucaoEmSegundos())
                .thenReturn(3600.0);

        when(tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHoras())
                .thenReturn(1800.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorDemanda())
                .thenReturn(2.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorServidor())
                .thenReturn(3.0);

        List<IndicadorValorDTO> lista =
                indicadorProcessoService.montarProcessoGeral();

        assertEquals(5, lista.size());
    }

    @Test
    void deveRetornarPercentualZeroPorServidorQuandoNaoExistiremDemandas() {
        when(demandaRepository.countByServicoSolicitanteId(1L))
                .thenReturn(0.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidasPorServidor(1L);

        assertEquals(0.0, dto.getValor());
    }

    @Test
    void deveCalcularPercentualDemandasResolvidasPorServidor() {
        when(demandaRepository.countByServicoSolicitanteId(1L))
                .thenReturn(20.0);

        when(demandaRepository.countByStatusAndServicoResponsavelId(
                StatusDemanda.FINALIZADA, 1L))
                .thenReturn(5.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidasPorServidor(1L);

        assertEquals(25.0, dto.getValor());
    }

    @Test
    void deveRetornarPercentualZeroPorServicoQuandoNaoExistiremDemandas() {
        when(demandaRepository.countByServicoResponsavelId(1L))
                .thenReturn(0.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidasPorServico(1L);

        assertEquals(0.0, dto.getValor());
    }

    @Test
    void deveCalcularPercentualDemandasResolvidasPorServico() {
        when(demandaRepository.countByServicoResponsavelId(1L))
                .thenReturn(10.0);

        when(demandaRepository.countByStatusAndServicoResponsavelId(
                StatusDemanda.FINALIZADA, 1L))
                .thenReturn(4.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidasPorServico(1L);

        assertEquals(40.0, dto.getValor());
    }

    @Test
    void deveRetornarPercentualZeroPorPeriodoQuandoNaoExistiremDemandas() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countByDataHoraCriacaoBetween(inicio, fim))
                .thenReturn(0.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidasPorPeriodo(inicio, fim);

        assertEquals(0.0, dto.getValor());
    }

    @Test
    void deveCalcularPercentualDemandasResolvidasPorPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countByDataHoraCriacaoBetween(inicio, fim))
                .thenReturn(8.0);

        when(demandaRepository.countByStatusAndDataHoraCriacaoBetween(
                StatusDemanda.FINALIZADA,
                inicio,
                fim))
                .thenReturn(2.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidasPorPeriodo(inicio, fim);

        assertEquals(25.0, dto.getValor());
    }

    @Test
    void deveRetornarPercentualZeroPorServicoEPeriodoQuandoNaoExistiremDemandas() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countByServicoResponsavelIdAndDataHoraCriacaoBetween(
                1L, inicio, fim))
                .thenReturn(0.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidasPorServicoEPeriodo(
                        1L, inicio, fim);

        assertEquals(0.0, dto.getValor());
    }

    @Test
    void deveCalcularPercentualDemandasResolvidasPorServicoEPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countByServicoResponsavelIdAndDataHoraCriacaoBetween(
                1L, inicio, fim))
                .thenReturn(12.0);

        when(demandaRepository.countByStatusAndServicoResponsavelIdAndDataHoraCriacaoBetween(
                StatusDemanda.FINALIZADA,
                1L,
                inicio,
                fim))
                .thenReturn(3.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidasPorServicoEPeriodo(
                        1L, inicio, fim);

        assertEquals(25.0, dto.getValor());
    }

    @Test
    void deveRetornarPercentualZeroPorServidorEPeriodoQuandoNaoExistiremDemandas() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countByServicoSolicitanteIdAndDataHoraCriacaoBetween(
                1L, inicio, fim))
                .thenReturn(0.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidasPorServidorEPeriodo(
                        1L, inicio, fim);

        assertEquals(0.0, dto.getValor());
    }

    @Test
    void deveCalcularPercentualDemandasResolvidasPorServidorEPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countByServicoSolicitanteIdAndDataHoraCriacaoBetween(
                1L, inicio, fim))
                .thenReturn(16.0);

        when(demandaRepository.countByStatusAndServicoSolicitanteIdAndDataHoraCriacaoBetween(
                StatusDemanda.FINALIZADA,
                1L,
                inicio,
                fim))
                .thenReturn(4.0);

        IndicadorValorDTO dto =
                indicadorProcessoService.percentualDemandasResolvidasPorServidorEPeriodo(
                        1L, inicio, fim);

        assertEquals(25.0, dto.getValor());
    }

    @Test
    void deveMontarProcessoPorServico() {
        when(demandaRepository.countByServicoResponsavelId(1L)).thenReturn(10.0);
        when(demandaRepository.countByStatusAndServicoResponsavelId(
                StatusDemanda.FINALIZADA, 1L))
                .thenReturn(5.0);

        when(demandaRepository.calcularTempoMedioResolucaoEmSegundosPorServico(1L))
                .thenReturn(3600.0);

        when(tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorServico(1L))
                .thenReturn(1800.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorDemandaPorServico(1L))
                .thenReturn(2.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorServidorPorServico(1L))
                .thenReturn(3.0);

        List<IndicadorValorDTO> resultado =
                indicadorProcessoService.montarProcessoPorServico(1L);

        assertEquals(5, resultado.size());
    }

    @Test
    void deveMontarProcessoPorServidor() {
        when(demandaRepository.countByServicoSolicitanteId(1L)).thenReturn(10.0);

        when(demandaRepository.countByStatusAndServicoResponsavelId(
                StatusDemanda.FINALIZADA, 1L))
                .thenReturn(5.0);

        when(demandaRepository.calcularTempoMedioResolucaoEmSegundosPorServicoSolicitante(1L))
                .thenReturn(3600.0);

        when(tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorServicoSolicitante(1L))
                .thenReturn(1800.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorDemandaPorServicoSolicitante(1L))
                .thenReturn(2.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorServidorPorServicoSolicitante(1L))
                .thenReturn(3.0);

        List<IndicadorValorDTO> resultado =
                indicadorProcessoService.montarProcessoPorServidor(1L);

        assertEquals(5, resultado.size());
    }

    @Test
    void deveMontarProcessoPorPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countByDataHoraCriacaoBetween(inicio, fim))
                .thenReturn(10.0);

        when(demandaRepository.countByStatusAndDataHoraCriacaoBetween(
                StatusDemanda.FINALIZADA, inicio, fim))
                .thenReturn(5.0);

        when(demandaRepository.calcularTempoMedioResolucaoEmSegundosPorPeriodo(inicio, fim))
                .thenReturn(3600.0);

        when(tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorPeriodo(inicio, fim))
                .thenReturn(1800.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorDemandaPorPeriodo(inicio, fim))
                .thenReturn(2.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorServidorPorPeriodo(inicio, fim))
                .thenReturn(3.0);

        List<IndicadorValorDTO> resultado =
                indicadorProcessoService.montarProcessoPorPeriodo(inicio, fim);

        assertEquals(5, resultado.size());
    }

    @Test
    void deveMontarProcessoPorServicoEPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countByServicoResponsavelIdAndDataHoraCriacaoBetween(
                1L, inicio, fim))
                .thenReturn(10.0);

        when(demandaRepository.countByStatusAndServicoResponsavelIdAndDataHoraCriacaoBetween(
                StatusDemanda.FINALIZADA, 1L, inicio, fim))
                .thenReturn(5.0);

        when(demandaRepository.calcularTempoMedioResolucaoEmSegundosPorServicoEPeriodo(
                1L, inicio, fim))
                .thenReturn(3600.0);

        when(tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorServicoEPeriodo(
                1L, inicio, fim))
                .thenReturn(1800.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorDemandaPorServicoEPeriodo(
                1L, inicio, fim))
                .thenReturn(2.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorServidorPorServicoEPeriodo(
                1L, inicio, fim))
                .thenReturn(3.0);

        List<IndicadorValorDTO> resultado =
                indicadorProcessoService.montarProcessoPorServicoEPeriodo(1L, inicio, fim);

        assertEquals(5, resultado.size());
    }

    @Test
    void deveMontarProcessoPorServidorEPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countByServicoSolicitanteIdAndDataHoraCriacaoBetween(
                1L, inicio, fim))
                .thenReturn(10.0);

        when(demandaRepository.countByStatusAndServicoSolicitanteIdAndDataHoraCriacaoBetween(
                StatusDemanda.FINALIZADA, 1L, inicio, fim))
                .thenReturn(5.0);

        when(demandaRepository.calcularTempoMedioResolucaoEmSegundosPorServicoSolicitanteEPeriodo(
                1L, inicio, fim))
                .thenReturn(3600.0);

        when(tentativaContatoRepository.calcularTempoMedioAtePrimeiraTentativaEmHorasPorServicoSolicitanteEPeriodo(
                1L, inicio, fim))
                .thenReturn(1800.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorDemandaPorServicoSolicitanteEPeriodo(
                1L, inicio, fim))
                .thenReturn(2.0);

        when(tentativaContatoRepository.calcularMediaTentativasPorServidorPorServicoSolicitanteEPeriodo(
                1L, inicio, fim))
                .thenReturn(3.0);

        List<IndicadorValorDTO> resultado =
                indicadorProcessoService.montarProcessoPorServidorEPeriodo(1L, inicio, fim);

        assertEquals(5, resultado.size());
    }

}

 */