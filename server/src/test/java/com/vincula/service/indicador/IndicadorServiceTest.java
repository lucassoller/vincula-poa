package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorDTO;
import com.vincula.entity.Servidor;
import com.vincula.entity.UnidadeSaude;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private IndicadorInsucessoService indicadorInsucessoService;

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

    private void autenticarUsuario() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

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
    void devePermitirAcessoUnidadeParaGestaoMunicipal() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(
                        indicadorService,
                        "validarAcessoUnidade",
                        1L
                )
        );
    }

    @Test
    void devePermitirAcessoQuandoServidorForDaMesmaUnidade() {
        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);

        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.SERVIDOR_APS);
        servidor.setUnidadeSaude(unidade);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(
                        indicadorService,
                        "validarAcessoUnidade",
                        1L
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoServidorAcessaOutraUnidade() {
        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);

        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.SERVIDOR_APS);
        servidor.setUnidadeSaude(unidade);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertThrows(
                BusinessException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        indicadorService,
                        "validarAcessoUnidade",
                        2L
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoServidorNaoPossuiUnidade() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.SERVIDOR_APS);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertThrows(
                BusinessException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        indicadorService,
                        "validarAcessoUnidade",
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
        when(indicadorInsucessoService.principaisMotivosInsucesso()).thenReturn(List.of());
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

        doReturn(dto).when(spy).indicadorPorUnidadeSolicitante(1L);

        IndicadorDTO resultado =
                spy.indicadorGeral(null, null, null, 1L);

        assertSame(dto, resultado);
    }


    @Test
    void deveBuscarIndicadorPorUnidadeSolicitanteEPeriodo() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy)
                .indicadorPorUnidadeSolicitanteEPeriodo(inicio, fim, 1L);

        IndicadorDTO resultado =
                spy.indicadorGeral(null, inicio, fim, 1L);

        assertSame(dto, resultado);
    }

    @Test
    void deveBuscarIndicadorPorUnidade() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy).indicadorPorUnidade(1L);

        IndicadorDTO resultado =
                spy.indicadorGeral(1L, null, null, null);

        assertSame(dto, resultado);
    }

    @Test
    void deveBuscarIndicadorPorUnidadeEPeriodo() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy)
                .indicadorPorUnidadeEPeriodo(1L, inicio, fim);

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
    void deveExportarIndicadorPorUnidade() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy).indicadorPorUnidade(1L);

        when(csvExporter.exportar(dto)).thenReturn("csv");

        String resultado = spy.exportarIndicadorPorUnidadeCsv(1L);

        assertEquals("csv", resultado);

        verify(auditoriaFacade)
                .exportacaoCsvRealizada("Indicador da unidade ID 1 exportado");
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
    void deveExportarIndicadorPorUnidadeEPeriodo() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy)
                .indicadorPorUnidadeEPeriodo(1L, inicio, fim);

        when(csvExporter.exportar(dto)).thenReturn("csv");

        String resultado =
                spy.exportarIndicadorPorUnidadeEPeriodoCsv(1L, inicio, fim);

        assertEquals("csv", resultado);

        verify(auditoriaFacade).exportacaoCsvRealizada(anyString());
    }

    @Test
    void deveExportarIndicadorPorUnidadeSolicitanteEPeriodo() {
        IndicadorDTO dto = mock(IndicadorDTO.class);

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dto).when(spy)
                .indicadorPorUnidadeSolicitanteEPeriodo(inicio, fim, 1L);

        when(csvExporter.exportar(dto)).thenReturn("csv");

        String resultado =
                spy.exportarIndicadorPorUnidadeSolicitanteEPeriodoCsv(1L, inicio, fim);

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

        when(indicadorProducaoService.indicadoresPorUnidadeSolicitante(servidorId))
                .thenReturn(List.of());

        IndicadorDTO result = indicadorService.indicadorPorUnidadeSolicitante(servidorId);

        assertNotNull(result);
    }

    @Test
    void deveMontarIndicadorPorUnidade() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        Long unidadeId = 10L;

        when(indicadorProducaoService.indicadoresPorUnidade(unidadeId))
                .thenReturn(List.of());

        IndicadorDTO result = indicadorService.indicadorPorUnidade(unidadeId);

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
    void deveMontarIndicadorPorUnidadeEPeriodo() {
        Servidor servidor = new Servidor();
        servidor.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        Long unidadeId = 10L;
        LocalDateTime ini = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        when(indicadorProducaoService.indicadoresPorUnidadeEPeriodo(unidadeId, ini, fim))
                .thenReturn(List.of());

        IndicadorDTO result =
                indicadorService.indicadorPorUnidadeEPeriodo(unidadeId, ini, fim);

        assertNotNull(result);
    }

    @Test
    void deveMontarIndicadorPorUnidadeSolicitanteEPeriodo() {
        Long servidorId = 1L;

        LocalDateTime ini = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        when(indicadorProducaoService.indicadoresPorServidorEPeriodo(servidorId, ini, fim))
                .thenReturn(List.of());

        IndicadorDTO result =
                indicadorService.indicadorPorUnidadeSolicitanteEPeriodo(ini, fim, servidorId);

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
                .exportarIndicadorPorUnidadeSolicitanteEPeriodoCsv(servidorId, ini, fim);

        String result = spy.exportarIndicadorGeralCsv(null, ini, fim, servidorId);

        assertEquals("csv-servidor-periodo", result);
    }

    @Test
    void deveExportarServidorSemPeriodo() {
        Long servidorId = 1L;

        IndicadorService spy = Mockito.spy(indicadorService);

        IndicadorDTO dtoMock = mock(IndicadorDTO.class);

        doReturn(dtoMock).when(spy).indicadorPorUnidadeSolicitante(servidorId);
        when(csvExporter.exportar(dtoMock)).thenReturn("csv-servidor");

        String result = spy.exportarIndicadorGeralCsv(null, null, null, servidorId);

        verify(auditoriaFacade)
                .exportacaoCsvRealizada(contains("servidor"));

        assertEquals("csv-servidor", result);
    }

    @Test
    void deveExportarUnidadeComPeriodo() {
        Long unidadeId = 10L;
        LocalDateTime ini = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now();

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn("csv-unidade-periodo")
                .when(spy)
                .exportarIndicadorPorUnidadeEPeriodoCsv(unidadeId, ini, fim);

        String result = spy.exportarIndicadorGeralCsv(unidadeId, ini, fim, null);

        assertEquals("csv-unidade-periodo", result);
    }

    @Test
    void deveExportarUnidadeSemPeriodo() {
        Long unidadeId = 10L;

        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn("csv-unidade")
                .when(spy)
                .exportarIndicadorPorUnidadeCsv(unidadeId);

        String result = spy.exportarIndicadorGeralCsv(unidadeId, null, null, null);

        assertEquals("csv-unidade", result);
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
                indicadorService.indicadorPorUnidade(10L)
        );
    }

    @Test
    void devePermitirAcessoServidorApsNaMesmaUnidade() {
        Long unidadeId = 10L;

        UnidadeSaude unidade = mock(UnidadeSaude.class);
        when(unidade.getId()).thenReturn(unidadeId);

        Servidor servidor = mock(Servidor.class);
        when(servidor.getPerfil()).thenReturn(PerfilServidor.SERVIDOR_APS);
        when(servidor.getUnidadeSaude()).thenReturn(unidade);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        assertDoesNotThrow(() ->
                indicadorService.indicadorPorUnidade(unidadeId)
        );
    }

    @Test
    void deveLancarExcecaoQuandoServidorApsAcessaOutraUnidade() {
        Long unidadeId = 10L;

        UnidadeSaude outraUnidade = mock(UnidadeSaude.class);
        when(outraUnidade.getId()).thenReturn(99L);

        Servidor servidor = mock(Servidor.class);
        when(servidor.getPerfil()).thenReturn(PerfilServidor.SERVIDOR_APS);
        when(servidor.getUnidadeSaude()).thenReturn(outraUnidade);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> indicadorService.indicadorPorUnidade(unidadeId)
        );

        assertEquals("Servidor não pode acessar indicadores de outra unidade", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoParaPerfilNaoAutorizado() {
        Long unidadeId = 10L;

        Servidor servidor = mock(Servidor.class);
        when(servidor.getPerfil()).thenReturn(PerfilServidor.SOLICITANTE);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> indicadorService.indicadorPorUnidade(unidadeId)
        );

        assertEquals("Servidor não pode acessar indicadores por unidade", ex.getMessage());
    }

    @Test
    void deveExecutarFluxoGeralSemParametros() {

        IndicadorDTO dtoMock = mock(IndicadorDTO.class);
        IndicadorService spy = Mockito.spy(indicadorService);

        doReturn(dtoMock)
                .when(spy)
                .indicadorGeral();

        when(csvExporter.exportar(dtoMock)).thenReturn("csv-geral");

        String result =
                spy.exportarIndicadorGeralCsv(null, null, null, null);

        assertEquals("csv-geral", result);
    }
}