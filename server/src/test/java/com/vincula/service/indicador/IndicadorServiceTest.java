/*package com.vincula.service.indicador;

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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

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
    private ServidorService servidorService;

    @Mock
    private IndicadorExporter csvExporter;

    @Mock
    private IndicadorRankingService indicadorRankingService;

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @Mock
    private IndicadorPrazoService indicadorPrazoService;

    @InjectMocks
    private IndicadorService indicadorService;

    private final LocalDateTime inicio = LocalDateTime.now().minusDays(30);
    private final LocalDateTime fim = LocalDateTime.now();

    @Test
    void devePermitirIndicadorGeralParaGestaoMunicipal() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(
                        indicadorService,
                        "validarAcessoIndicadorGeral"
                )
        );
    }
    @Test
    void deveLancarExcecaoQuandoNaoForGestaoMunicipal() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.SERVIDOR_APS);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertThrows(
                BusinessException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        indicadorService,
                        "validarAcessoIndicadorGeral"
                )
        );
    }

    @Test
    void devePermitirAcessoServicoParaGestaoMunicipal() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(
                        indicadorService,
                        "validarAcessoServico",
                        1L
                )
        );
    }

    @Test
    void devePermitirAcessoQuandoServidorForDaMesmaServico() {
        Servico servico = new Servico();
        servico.setId(1L);

        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.SERVIDOR_APS);
        servidor.setServico(servico);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(
                        indicadorService,
                        "validarAcessoServico",
                        1L
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoServidorAcessaOutraServico() {
        Servico servico = new Servico();
        servico.setId(1L);

        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.SERVIDOR_APS);
        servidor.setServico(servico);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertThrows(
                BusinessException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        indicadorService,
                        "validarAcessoServico",
                        2L
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoServidorNaoPossuiServico() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.SERVIDOR_APS);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertThrows(
                BusinessException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        indicadorService,
                        "validarAcessoServico",
                        1L
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoPeriodoIncompleto() {
        assertThrows(
                BusinessException.class,
                () -> indicadorService.indicadorGeral(
                        null,
                        LocalDateTime.now(),
                        null,
                        null
                )
        );
    }

    @Test
    void deveLancarExcecaoAoExportarQuandoPeriodoIncompleto() {
        assertThrows(
                BusinessException.class,
                () -> indicadorService.exportarIndicadorGeralCsv(
                        null,
                        LocalDateTime.now(),
                        null,
                        null
                )
        );
    }

    @Test
    void deveMontarIndicadorGeral() {
        when(indicadorProducaoService.indicadoresGerais()).thenReturn(List.of());
        when(indicadorProcessoService.montarProcessoGeral()).thenReturn(List.of());
        when(indicadorResultadoService.percentualPorDesfecho()).thenReturn(List.of());
        when(indicadorMotivoService.principaisMotivos()).thenReturn(List.of());
        when(indicadorComplementoService.principaisComplementos()).thenReturn(List.of());
        when(indicadorPrazoService.indicadoresPrazo()).thenReturn(List.of());

        IndicadorDTO dto = indicadorService.indicadorGeral();

        assertNotNull(dto);

        verify(indicadorRankingService).rankingPorTotalDemandas();
        verify(indicadorRankingService).rankingPorPercentualResolucao();
        verify(indicadorRankingService).rankingPorTempoMedioResolucao();
        verify(indicadorRankingService).rankingPorTempoAtePrimeiraTentativa();
    }

    @Test
    void deveBuscarIndicadorPorServidor() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy).indicadorPorServicoSolicitante(1L);

        IndicadorDTO resultado =
                spy.indicadorGeral(null, null, null, 1L);

        assertSame(dto, resultado);
    }


    @Test
    void deveBuscarIndicadorPorServicoSolicitanteEPeriodo() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy)
                .indicadorPorServicoSolicitanteEPeriodo(inicio, fim, 1L);

        IndicadorDTO resultado =
                spy.indicadorGeral(null, inicio, fim, 1L);

        assertSame(dto, resultado);
    }

    @Test
    void deveBuscarIndicadorPorServico() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy).indicadorPorServico(1L);

        IndicadorDTO resultado =
                spy.indicadorGeral(1L, null, null, null);

        assertSame(dto, resultado);
    }

    @Test
    void deveBuscarIndicadorPorServicoEPeriodo() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy)
                .indicadorPorServicoEPeriodo(1L, inicio, fim);

        IndicadorDTO resultado =
                spy.indicadorGeral(1L, inicio, fim, null);

        assertSame(dto, resultado);
    }

    @Test
    void deveBuscarIndicadorPorPeriodo() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy)
                .indicadorPorPeriodo(inicio, fim);

        IndicadorDTO resultado =
                spy.indicadorGeral(null, inicio, fim, null);

        assertSame(dto, resultado);
    }

    @Test
    void deveExportarIndicadorPorServico() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy).indicadorPorServico(1L);

        when(csvExporter.exportar(dto)).thenReturn("csv");

        String resultado = spy.exportarIndicadorPorServicoCsv(1L);

        assertEquals("csv", resultado);

        verify(auditoriaFacade)
                .exportacaoCsvRealizada("Indicador da servico ID 1 exportado");
    }

    @Test
    void deveExportarIndicadorPorPeriodo() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy)
                .indicadorPorPeriodo(inicio, fim);

        when(csvExporter.exportar(dto)).thenReturn("csv");

        String resultado =
                spy.exportarIndicadorPorPeriodoCsv(inicio, fim);

        assertEquals("csv", resultado);

        verify(auditoriaFacade).exportacaoCsvRealizada(anyString());
    }

    @Test
    void deveExportarIndicadorPorServicoEPeriodo() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy)
                .indicadorPorServicoEPeriodo(1L, inicio, fim);

        when(csvExporter.exportar(dto)).thenReturn("csv");

        String resultado =
                spy.exportarIndicadorPorServicoEPeriodoCsv(1L, inicio, fim);

        assertEquals("csv", resultado);

        verify(auditoriaFacade).exportacaoCsvRealizada(anyString());
    }

    @Test
    void deveExportarIndicadorPorServicoSolicitanteEPeriodo() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy)
                .indicadorPorServicoSolicitanteEPeriodo(inicio, fim, 1L);

        when(csvExporter.exportar(dto)).thenReturn("csv");

        String resultado =
                spy.exportarIndicadorPorServicoSolicitanteEPeriodoCsv(1L, inicio, fim);

        assertEquals("csv", resultado);

        verify(auditoriaFacade).exportacaoCsvRealizada(anyString());
    }

    @Test
    void deveExportarIndicadorGeral() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy).indicadorGeral();

        when(csvExporter.exportar(dto)).thenReturn("csv");

        String resultado =
                spy.exportarIndicadorGeralCsv(null, null, null, null);

        assertEquals("csv", resultado);

        verify(auditoriaFacade)
                .exportacaoCsvRealizada("Indicador geral exportado");
    }

    @Test
    void deveMontarIndicadorPorServidor() {
        Long servidorId = 1L;

        when(indicadorProducaoService.indicadoresPorServicoSolicitante(servidorId))
                .thenReturn(List.of());

        IndicadorDTO result = indicadorService.indicadorPorServicoSolicitante(servidorId);

        assertNotNull(result);
    }

    @Test
    void deveMontarIndicadorPorServico() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        Long servicoId = 10L;

        when(indicadorProducaoService.indicadoresPorServico(servicoId))
                .thenReturn(List.of());

        IndicadorDTO result = indicadorService.indicadorPorServico(servicoId);

        assertNotNull(result);
    }

    @Test
    void deveMontarIndicadorPorPeriodo() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        LocalDateTime ini = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        when(indicadorProducaoService.indicadoresPorPeriodo(ini, fim))
                .thenReturn(List.of());

        IndicadorDTO result = indicadorService.indicadorPorPeriodo(ini, fim);

        assertNotNull(result);
    }

    @Test
    void deveMontarIndicadorPorServicoEPeriodo() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        Long servicoId = 10L;
        LocalDateTime ini = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        when(indicadorProducaoService.indicadoresPorServicoEPeriodo(servicoId, ini, fim))
                .thenReturn(List.of());

        IndicadorDTO result =
                indicadorService.indicadorPorServicoEPeriodo(servicoId, ini, fim);

        assertNotNull(result);
    }

    @Test
    void deveMontarIndicadorPorServicoSolicitanteEPeriodo() {
        Long servidorId = 1L;

        LocalDateTime ini = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        when(indicadorProducaoService.indicadoresPorServidorEPeriodo(servidorId, ini, fim))
                .thenReturn(List.of());

        IndicadorDTO result =
                indicadorService.indicadorPorServicoSolicitanteEPeriodo(ini, fim, servidorId);

        assertNotNull(result);
    }

    @Test
    void deveExportarServidorComPeriodo() {
        Long servidorId = 1L;
        LocalDateTime ini = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn("csv-servidor-periodo")
                .when(spy)
                .exportarIndicadorPorServicoSolicitanteEPeriodoCsv(servidorId, ini, fim);

        String result = spy.exportarIndicadorGeralCsv(null, ini, fim, servidorId);

        assertEquals("csv-servidor-periodo", result);
    }

    @Test
    void deveExportarServidorSemPeriodo() {
        Long servidorId = 1L;

        IndicadorService spy = Mockito.spy(indicadorService);

        IndicadorDTO dtoMock = mock(IndicadorDTO.class);

        doReturn(dtoMock).when(spy).indicadorPorServicoSolicitante(servidorId);
        when(csvExporter.exportar(dtoMock)).thenReturn("csv-servidor");

        String result = spy.exportarIndicadorGeralCsv(null, null, null, servidorId);

        verify(auditoriaFacade)
                .exportacaoCsvRealizada(contains("servidor"));

        assertEquals("csv-servidor", result);
    }

    @Test
    void deveExportarServicoComPeriodo() {
        Long servicoId = 10L;
        LocalDateTime ini = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn("csv-servico-periodo")
                .when(spy)
                .exportarIndicadorPorServicoEPeriodoCsv(servicoId, ini, fim);

        String result = spy.exportarIndicadorGeralCsv(servicoId, ini, fim, null);

        assertEquals("csv-servico-periodo", result);
    }

    @Test
    void deveExportarServicoSemPeriodo() {
        Long servicoId = 10L;

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn("csv-servico")
                .when(spy)
                .exportarIndicadorPorServicoCsv(servicoId);

        String result = spy.exportarIndicadorGeralCsv(servicoId, null, null, null);

        assertEquals("csv-servico", result);
    }

    @Test
    void deveExportarApenasPeriodo() {
        LocalDateTime ini = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        IndicadorService spy = Mockito.spy(indicadorService);

        IndicadorDTO dtoMock = mock(IndicadorDTO.class);

        doReturn(dtoMock)
                .when(spy)
                .indicadorPorPeriodo(ini, fim);

        when(csvExporter.exportar(dtoMock)).thenReturn("csv-periodo");

        String result = spy.exportarIndicadorGeralCsv(null, ini, fim, null);

        assertEquals("csv-periodo", result);
    }

    @Test
    void deveExportarGeral() {
        IndicadorDTO dtoMock = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dtoMock).when(spy).indicadorGeral();
        when(csvExporter.exportar(dtoMock)).thenReturn("csv-geral");

        String result = spy.exportarIndicadorGeralCsv(null, null, null, null);

        verify(auditoriaFacade)
                .exportacaoCsvRealizada("Indicador geral exportado");

        assertEquals("csv-geral", result);
    }

    @Test
    void devePermitirAcessoGestaoMunicipal() {
        Servidor servidor = mock(Servidor.class);
        when(servidor.getPerfil()).thenReturn(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertDoesNotThrow(() ->
                indicadorService.indicadorPorServico(10L)
        );
    }

    @Test
    void devePermitirAcessoServidorApsNaMesmaServico() {
        Long servicoId = 10L;

        Servico servico = mock(Servico.class);
        when(servico.getId()).thenReturn(servicoId);

        Servidor servidor = mock(Servidor.class);
        when(servidor.getPerfil()).thenReturn(PerfilServidor.SERVIDOR_APS);
        when(servidor.getServico()).thenReturn(servico);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertDoesNotThrow(() ->
                indicadorService.indicadorPorServico(servicoId)
        );
    }

    @Test
    void deveLancarExcecaoQuandoServidorApsAcessaOutraServico() {
        Long servicoId = 10L;

        Servico outraServico = mock(Servico.class);
        when(outraServico.getId()).thenReturn(99L);

        Servidor servidor = mock(Servidor.class);
        when(servidor.getPerfil()).thenReturn(PerfilServidor.SERVIDOR_APS);
        when(servidor.getServico()).thenReturn(outraServico);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> indicadorService.indicadorPorServico(servicoId)
        );

        assertEquals("Servidor não pode acessar indicadores de outra servico", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaPerfilNaoAutorizado() {
        Long servicoId = 10L;

        Servidor servidor = mock(Servidor.class);
        when(servidor.getPerfil()).thenReturn(PerfilServidor.SOLICITANTE);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> indicadorService.indicadorPorServico(servicoId)
        );

        assertEquals("Servidor não pode acessar indicadores de outra servico", ex.getMessage());
    }

    @Test
    void deveExecutarFluxoIndicadorGeralSemFiltros() {

        IndicadorDTO dtoMock = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        Servidor servidor = mock(Servidor.class);
        when(servidor.getPerfil()).thenReturn(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        doReturn(dtoMock)
                .when(spy)
                .indicadorGeral();

        IndicadorDTO resultado =
                spy.indicadorGeral(null, null, null, null);

        assertEquals(dtoMock, resultado);

        verify(spy).indicadorGeral();
    }
}

 */