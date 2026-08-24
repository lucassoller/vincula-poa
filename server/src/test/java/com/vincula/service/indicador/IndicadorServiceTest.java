package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.IndicadorDTO;
import com.vincula.entity.Servidor;
import com.vincula.entity.Servico;
import com.vincula.enums.PerfilServidor;
import com.vincula.exception.BusinessException;
import com.vincula.export.IndicadorExporter;
import com.vincula.service.ServidorService;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorServiceTest {

    @Mock
    private IndicadorProducaoService indicadorProducaoService;

    @Mock
    private IndicadorProcessoService indicadorProcessoService;

    @Mock
    private IndicadorResultadoService indicadorResultadoService;

    @Mock
    private IndicadorMotivoService indicadorMotivoService;

    @Mock
    private IndicadorComplementoService indicadorComplementoService;

    @Mock
    private IndicadorRankingService indicadorRankingService;

    @Mock
    private IndicadorPrazoService indicadorPrazoService;

    @Mock
    private IndicadorExporter csvExporter;

    @Mock
    private ServidorService servidorService;

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @InjectMocks
    private IndicadorService indicadorService;

    @Test
    void deveGerarIndicadoresParaGestaoMunicipal() {

        FiltroIndicadorRequestDTO filtro = mock(FiltroIndicadorRequestDTO.class);

        Servidor servidor = mock(Servidor.class);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servidor.getPerfil())
                .thenReturn(PerfilServidor.GESTAO_MUNICIPAL);

        IndicadorDTO resultado = indicadorService.gerarIndicadores(filtro);

        assertNotNull(resultado);

        verify(indicadorProducaoService)
                .gerarIndicadores(filtro);

        verify(indicadorProcessoService)
                .gerarIndicadores(filtro);

        verify(indicadorResultadoService)
                .gerarIndicadores(filtro);

        verify(indicadorMotivoService)
                .gerarIndicadores(filtro);

        verify(indicadorComplementoService)
                .gerarIndicadores(filtro);

        verify(indicadorPrazoService)
                .gerarIndicadores(filtro);

        verify(indicadorRankingService)
                .gerarRankingPorTotalDemandas();

        verify(indicadorRankingService)
                .gerarRankingPorPercentualResolucao();

        verify(indicadorRankingService)
                .gerarRankingPorTempoMedioResolucao();

        verify(indicadorRankingService)
                .gerarRankingPorTempoAtePrimeiraTentativa();
    }


    @Test
    void deveGerarIndicadoresParaServidorAps() {

        FiltroIndicadorRequestDTO filtro = mock(FiltroIndicadorRequestDTO.class);

        Servidor servidor = mock(Servidor.class);
        Servico servico = mock(Servico.class);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servidor.getPerfil())
                .thenReturn(PerfilServidor.SERVIDOR_APS);

        when(servidor.getServico())
                .thenReturn(servico);

        when(servico.getId())
                .thenReturn(10L);

        IndicadorDTO resultado =
                indicadorService.gerarIndicadores(filtro);

        assertNotNull(resultado);

        verify(filtro)
                .setServicoResponsavelId(10L);

        verify(indicadorProducaoService)
                .gerarIndicadores(filtro);

        verify(indicadorRankingService, never())
                .gerarRankingPorTotalDemandas();

        verify(indicadorRankingService, never())
                .gerarRankingPorPercentualResolucao();

        verify(indicadorRankingService, never())
                .gerarRankingPorTempoMedioResolucao();

        verify(indicadorRankingService, never())
                .gerarRankingPorTempoAtePrimeiraTentativa();
    }


    @Test
    void deveGerarIndicadoresParaSolicitante() {

        FiltroIndicadorRequestDTO filtro = mock(FiltroIndicadorRequestDTO.class);

        Servidor servidor = mock(Servidor.class);
        Servico servico = mock(Servico.class);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servidor.getPerfil())
                .thenReturn(PerfilServidor.SOLICITANTE);

        when(servidor.getServico())
                .thenReturn(servico);

        when(servico.getId())
                .thenReturn(20L);

        IndicadorDTO resultado =
                indicadorService.gerarIndicadores(filtro);

        assertNotNull(resultado);

        verify(filtro)
                .setServicoSolicitanteId(20L);

        verify(indicadorProducaoService)
                .gerarIndicadores(filtro);

        verify(indicadorRankingService, never())
                .gerarRankingPorTotalDemandas();
    }


    @Test
    void deveGerarIndicadoresParaVigilancia() {

        FiltroIndicadorRequestDTO filtro =
                mock(FiltroIndicadorRequestDTO.class);

        Servidor servidor =
                mock(Servidor.class);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servidor.getPerfil())
                .thenReturn(PerfilServidor.VIGILANCIA);

        IndicadorDTO resultado =
                indicadorService.gerarIndicadores(filtro);

        assertNotNull(resultado);

        verify(indicadorProducaoService)
                .gerarIndicadores(filtro);

        verify(indicadorRankingService, never())
                .gerarRankingPorTotalDemandas();

        verify(indicadorRankingService, never())
                .gerarRankingPorPercentualResolucao();
    }

    @Test
    void deveGerarIndicadoresParaCoordenadoria() {

        FiltroIndicadorRequestDTO filtro =
                mock(FiltroIndicadorRequestDTO.class);

        Servidor servidor =
                mock(Servidor.class);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servidor.getPerfil())
                .thenReturn(PerfilServidor.COORDENADORIA);

        IndicadorDTO resultado =
                indicadorService.gerarIndicadores(filtro);

        assertNotNull(resultado);

        verify(indicadorProducaoService)
                .gerarIndicadores(filtro);

        verify(indicadorRankingService, never())
                .gerarRankingPorTotalDemandas();
    }

    @Test
    void deveAceitarFiltroComDatasValidas() {

        FiltroIndicadorRequestDTO filtro =
                mock(FiltroIndicadorRequestDTO.class);

        when(filtro.getDataInicial())
                .thenReturn(LocalDate.of(2026, 1, 1));

        when(filtro.getDataFinal())
                .thenReturn(LocalDate.of(2026, 1, 31));

        Servidor servidor =
                mock(Servidor.class);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servidor.getPerfil())
                .thenReturn(PerfilServidor.GESTAO_MUNICIPAL);

        assertDoesNotThrow(() ->
                indicadorService.gerarIndicadores(filtro)
        );
    }


    @Test
    void deveAceitarFiltroSemDatas() {

        FiltroIndicadorRequestDTO filtro =
                mock(FiltroIndicadorRequestDTO.class);

        when(filtro.getDataInicial())
                .thenReturn(null);

        when(filtro.getDataFinal())
                .thenReturn(null);

        Servidor servidor =
                mock(Servidor.class);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servidor.getPerfil())
                .thenReturn(PerfilServidor.GESTAO_MUNICIPAL);

        assertDoesNotThrow(() ->
                indicadorService.gerarIndicadores(filtro)
        );
    }


    @Test
    void deveLancarExcecaoQuandoDataInicialForPosteriorADataFinal() {

        FiltroIndicadorRequestDTO filtro =
                mock(FiltroIndicadorRequestDTO.class);

        when(filtro.getDataInicial())
                .thenReturn(LocalDate.of(2026, 2, 1));

        when(filtro.getDataFinal())
                .thenReturn(LocalDate.of(2026, 1, 1));

        Servidor servidor =
                mock(Servidor.class);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> indicadorService.gerarIndicadores(filtro)
        );

        assertEquals(
                "A data inicial deve ser anterior à data final.",
                exception.getMessage()
        );
    }


    // =========================================================
    // Exportação CSV
    // =========================================================

    @Test
    void deveExportarIndicadoresCsv() {

        FiltroIndicadorRequestDTO filtro =
                mock(FiltroIndicadorRequestDTO.class);

        Servidor servidor =
                mock(Servidor.class);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servidor.getPerfil())
                .thenReturn(PerfilServidor.GESTAO_MUNICIPAL);

        when(csvExporter.exportar(any(IndicadorDTO.class)))
                .thenReturn("id,nome\n1,Teste");

        String resultado =
                indicadorService.exportarIndicadoresCsv(filtro);

        assertEquals(
                "id,nome\n1,Teste",
                resultado
        );

        verify(csvExporter)
                .exportar(any(IndicadorDTO.class));

        verify(auditoriaFacade)
                .exportacaoCsvRealizada("Indicador exportado");
    }


    @Test
    void deveValidarFiltroAntesDeExportar() {

        FiltroIndicadorRequestDTO filtro =
                mock(FiltroIndicadorRequestDTO.class);

        when(filtro.getDataInicial())
                .thenReturn(LocalDate.of(2026, 5, 1));

        when(filtro.getDataFinal())
                .thenReturn(LocalDate.of(2026, 4, 1));

        Servidor servidor =
                mock(Servidor.class);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertThrows(
                BusinessException.class,
                () -> indicadorService.exportarIndicadoresCsv(filtro)
        );

        verify(csvExporter, never())
                .exportar(any());

        verify(auditoriaFacade, never())
                .exportacaoCsvRealizada(anyString());
    }
}