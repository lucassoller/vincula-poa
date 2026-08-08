package com.vincula.service;

import com.vincula.dto.MotivoBuscaResponseDTO;
import com.vincula.dto.demanda.DemandaDTO;
import com.vincula.dto.demanda.DemandaResponseDTO;
import com.vincula.dto.demanda.EncerrarDemandaDTO;
import com.vincula.dto.demanda.RedirecionarDemandaDTO;
import com.vincula.entity.Demanda;
import com.vincula.entity.Servidor;
import com.vincula.entity.Servico;
import com.vincula.entity.Usuario;
import com.vincula.enums.*;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.export.DemandaExporter;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.ServicoRepository;
import com.vincula.repository.UsuarioRepository;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemandaServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ServidorService servidorService;

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @Mock
    private DemandaExporter demandaExporter;

    @InjectMocks
    private DemandaService demandaService;

    private Demanda criarDemanda() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Lucas");

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("UBS Centro");

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Servidor Teste");

        Demanda demanda = new Demanda();
        demanda.setId(1L);
        demanda.setUsuario(usuario);
        demanda.setServicoResponsavel(servico);
        demanda.setServidorCriador(servidor);
        demanda.setStatus(StatusDemanda.ABERTA);
        demanda.setDataHoraCriacao(LocalDateTime.now());

        return demanda;
    }

    private Page<Demanda> criarPaginaDemanda() {
        return new PageImpl<>(List.of(criarDemanda()));
    }

    private List<Demanda> criarDemandas() {

        Demanda demanda = new Demanda();
        demanda.setId(1L);

        return List.of(demanda);
    }

    @Test
    void deveBuscarPorId() {

        Demanda demanda = new Demanda();
        demanda.setId(1L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Lucas");

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("UBS");

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Servidor");

        demanda.setUsuario(usuario);
        demanda.setServicoResponsavel(servico);
        demanda.setServidorCriador(servidor);
        demanda.setDataHoraCriacao(LocalDateTime.now());

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        DemandaResponseDTO dto =
                demandaService.buscarPorId(1L);

        assertEquals(1L, dto.getId());
    }

    @Test
    void deveLancarErroQuandoDemandaNaoEncontrada() {

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> demandaService.buscarPorId(1L)
        );
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoExiste() {

        DemandaDTO dto = new DemandaDTO();
        dto.setUsuarioId(1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> demandaService.criar(dto)
        );
    }

    @Test
    void deveLancarErroQuandoNovaServicoNaoExiste() {

        Demanda demanda = criarDemanda();

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        when(servicoRepository.findById(99L))
                .thenReturn(Optional.empty());

        RedirecionarDemandaDTO dto =
                new RedirecionarDemandaDTO();

        dto.setNovoServicoResponsavelId(99L);

        assertThrows(
                NotFoundException.class,
                () -> demandaService.redirecionar(1L, dto)
        );
    }

    @Test
    void deveLancarErroAoAtualizarDemandaFinalizada() {

        Demanda demanda = criarDemanda();
        demanda.setStatus(StatusDemanda.FINALIZADA);

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        assertThrows(
                BusinessException.class,
                () -> demandaService.atualizar(1L, new DemandaDTO())
        );
    }

    @Test
    void deveAtualizarDemanda() {

        Demanda demanda = criarDemanda();

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        when(demandaRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        DemandaDTO dto = new DemandaDTO();
        dto.setMotivoBuscaAtiva(MotivoBuscaAtiva.OUTRO);
        dto.setDescricaoBusca("teste");
        dto.setPrazoDemanda(PrazoDemanda.D7);

        DemandaResponseDTO response =
                demandaService.atualizar(1L, dto);

        assertEquals(
                MotivoBuscaAtiva.OUTRO,
                response.getMotivoBuscaAtiva()
        );

        verify(auditoriaFacade)
                .demandaAtualizada(eq(1L), anyString());
    }

    @Test
    void deveLancarErroAoEncerrarDemandaFinalizada() {

        Demanda demanda = criarDemanda();
        demanda.setStatus(StatusDemanda.FINALIZADA);

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        assertThrows(
                BusinessException.class,
                () -> demandaService.encerrar(
                        1L,
                        new EncerrarDemandaDTO()
                )
        );
    }

    @Test
    void deveLancarErroQuandoDesfechoNaoInformado() {

        Demanda demanda = criarDemanda();

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        EncerrarDemandaDTO dto =
                new EncerrarDemandaDTO();

        assertThrows(
                BusinessException.class,
                () -> demandaService.encerrar(1L, dto)
        );
    }

    @Test
    void deveEncerrarDemanda() {

        Demanda demanda = criarDemanda();

        Servidor servidor = new Servidor();
        servidor.setId(10L);
        servidor.setNome("Servidor");

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        EncerrarDemandaDTO dto =
                new EncerrarDemandaDTO();

        dto.setDesfechoDemanda(
                DesfechoDemanda.ENCONTRADO_VINCULADO
        );

        DemandaResponseDTO response =
                demandaService.encerrar(1L, dto);

        assertEquals(
                StatusDemanda.FINALIZADA,
                response.getStatus()
        );
    }



    /*
    @Test
    void deveListarTodasFiltradas() {

        Pageable pageable = PageRequest.of(0, 10);

        when(demandaRepository.findFiltradas("teste", pageable))
                .thenReturn(criarPaginaDemanda());

        Page<DemandaResponseDTO> resultado =
                demandaService.listarTodasFiltradas("teste", pageable);

        assertEquals(1, resultado.getTotalElements());
    }*/

    @Test
    void deveListarPorUsuario() {

        Pageable pageable = PageRequest.of(0, 10);

        when(demandaRepository.findByUsuarioOrderByUsuarioNome(1L, pageable))
                .thenReturn(criarPaginaDemanda());

        Page<DemandaResponseDTO> resultado =
                demandaService.listarPorUsuario(1L, pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    /*
    @Test
    void deveListarPorServicoFiltradas() {

        Pageable pageable = PageRequest.of(0, 10);

        when(demandaRepository.findFiltradasByServico(10L, "abc", pageable))
                .thenReturn(criarPaginaDemanda());

        Page<DemandaResponseDTO> resultado =
                demandaService.listarPorServicoFiltradas(10L, "abc", pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveListarPorServidorCriadorFiltradas() {

        Pageable pageable = PageRequest.of(0, 10);

        when(demandaRepository.findFiltradasByServicoSolicitante(
                1L,
                "abc",
                pageable))
                .thenReturn(criarPaginaDemanda());

        Page<DemandaResponseDTO> resultado =
                demandaService.listarPorServicoSolicitanteFiltradas(
                        1L,
                        "abc",
                        pageable);

        assertEquals(1, resultado.getTotalElements());
    }


     */

    @Test
    void deveCriarDemanda() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Servico servico = new Servico();
        servico.setId(10L);

        usuario.setServico(servico);

        Servidor servidor = new Servidor();
        servidor.setId(5L);

        DemandaDTO dto = new DemandaDTO();
        dto.setUsuarioId(1L);
        dto.setPrazoDemanda(PrazoDemanda.D7);
        dto.setPrioridade(Prioridade.BAIXA);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.save(any(Demanda.class)))
                .thenAnswer(i -> {
                    Demanda d = i.getArgument(0);
                    d.setId(100L);
                    return d;
                });

        DemandaResponseDTO response =
                demandaService.criar(dto);

        assertEquals(100L, response.getId());

        verify(auditoriaFacade)
                .demandaCriada(100L, 1L);
    }

    @Test
    void deveRedirecionarDemanda() {

        Servico atual = new Servico();
        atual.setId(1L);
        atual.setNome("UBS A");

        Servico nova = new Servico();
        nova.setId(2L);
        nova.setNome("UBS B");

        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setNomeCompleto("Lucas");
        usuario.setServico(atual);

        Servidor criador = new Servidor();
        criador.setId(5L);
        criador.setNome("Criador");

        Servidor servidorLogado = new Servidor();
        servidorLogado.setId(99L);
        servidorLogado.setNome("Servidor");

        Demanda demanda = new Demanda();
        demanda.setId(1L);
        demanda.setStatus(StatusDemanda.ABERTA);
        demanda.setUsuario(usuario);
        demanda.setServicoResponsavel(atual);
        demanda.setServidorCriador(criador);

        RedirecionarDemandaDTO dto = new RedirecionarDemandaDTO();
        dto.setNovoServicoResponsavelId(2L);
        dto.setMotivoRedirecionamento("Mudança");

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        when(servicoRepository.findById(2L))
                .thenReturn(Optional.of(nova));

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidorLogado);

        when(demandaRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        DemandaResponseDTO response =
                demandaService.redirecionar(1L, dto);

        assertEquals(2L, response.getServicoResponsavelId());
        assertTrue(response.getFoiRedirecionada());

        verify(auditoriaFacade)
                .demandaRedirecionada(eq(1L), anyString());
    }

    @Test
    void deveLancarErroQuandoDemandaFinalizada() {

        Demanda demanda = new Demanda();
        demanda.setStatus(StatusDemanda.FINALIZADA);

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        RedirecionarDemandaDTO dto = new RedirecionarDemandaDTO();
        dto.setNovoServicoResponsavelId(2L);

        assertThrows(
                BusinessException.class,
                () -> demandaService.redirecionar(1L, dto)
        );
    }

    @Test
    void deveLancarErroQuandoNovaServicoForIgualAtual() {

        Servico servico = new Servico();
        servico.setId(1L);

        Demanda demanda = new Demanda();
        demanda.setStatus(StatusDemanda.ABERTA);
        demanda.setServicoResponsavel(servico);

        RedirecionarDemandaDTO dto = new RedirecionarDemandaDTO();
        dto.setNovoServicoResponsavelId(1L);

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        when(servicoRepository.findById(1L))
                .thenReturn(Optional.of(servico));

        assertThrows(
                BusinessException.class,
                () -> demandaService.redirecionar(1L, dto)
        );
    }

    @Test
    void deveRedirecionarDemandasAbertasDoUsuario() {

        Servico origem = new Servico();
        origem.setId(1L);

        Servico destino = new Servico();
        destino.setId(2L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setServico(destino);

        Servidor servidor = new Servidor();
        servidor.setId(10L);

        Demanda demanda = new Demanda();
        demanda.setStatus(StatusDemanda.ABERTA);
        demanda.setServicoResponsavel(origem);
        demanda.setPrioridade(Prioridade.ALTA);

        RedirecionarDemandaDTO dto = new RedirecionarDemandaDTO();
        dto.setMotivoRedirecionamento("Teste");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.findByUsuarioIdAndStatusIn(
                eq(1L),
                anyList()))
                .thenReturn(List.of(demanda));

        demandaService.redirecionarDemandasAbertasDoUsuario(1L, dto);

        assertEquals(destino, demanda.getServicoResponsavel());
        assertTrue(demanda.getFoiRedirecionada());

        verify(demandaRepository)
                .saveAll(anyList());
    }

    @Test
    void naoDeveRedirecionarQuandoServicoJaForADoUsuario() {

        Servico servico = new Servico();
        servico.setId(1L);

        Usuario usuario = new Usuario();
        usuario.setServico(servico);

        Demanda demanda = new Demanda();
        demanda.setServicoResponsavel(servico);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(new Servidor());

        when(demandaRepository.findByUsuarioIdAndStatusIn(
                eq(1L),
                anyList()))
                .thenReturn(List.of(demanda));

        demandaService.redirecionarDemandasAbertasDoUsuario(
                1L,
                new RedirecionarDemandaDTO());

        assertNull(demanda.getServicoResponsavelAnterior());
        assertNull(demanda.getMotivoRedirecionamento());

        verify(demandaRepository)
                .saveAll(anyList());
    }

    @Test
    void deveDeletarDemanda() {

        Demanda demanda = new Demanda();
        demanda.setId(1L);

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        demandaService.deletar(1L);

        verify(demandaRepository).delete(demanda);

        verify(auditoriaFacade)
                .demandaDeletada(1L);
    }

    /*

    @Test
    void deveExportarDemandasCsv() {

        List<Demanda> demandas = criarDemandas();

        when(demandaRepository.findAllOrderByUsuarioNome())
                .thenReturn(demandas);

        when(demandaExporter.exportar(demandas))
                .thenReturn("csv");

        String resultado =
                demandaService.exportarDemandasCsv();

        assertEquals("csv", resultado);

        verify(auditoriaFacade)
                .exportacaoCsvRealizadaDemanda(
                        "Demandas exportadas"
                );
    }

    @Test
    void deveExportarDemandasFiltradasCsv() {

        List<Demanda> demandas = criarDemandas();

        when(demandaRepository.findFiltradas("teste"))
                .thenReturn(demandas);

        when(demandaExporter.exportar(demandas))
                .thenReturn("csv");

        String resultado =
                demandaService.exportarDemandasFiltradasCsv("teste");

        assertEquals("csv", resultado);

        verify(auditoriaFacade)
                .exportacaoCsvRealizadaDemanda(
                        "Demandas exportadas"
                );
    }

    @Test
    void deveExportarDemandasPorServicoCsv() {

        List<Demanda> demandas = criarDemandas();

        when(demandaRepository.findByServicoOrderByUsuarioNome(1L))
                .thenReturn(demandas);

        when(demandaExporter.exportar(demandas))
                .thenReturn("csv");

        String resultado =
                demandaService.exportarDemandasPorServicoCsv(1L);

        assertEquals("csv", resultado);

        verify(auditoriaFacade)
                .exportacaoCsvRealizadaDemanda(
                        "Demandas da servico 1 exportadas"
                );
    }

    @Test
    void deveExportarDemandasFiltradasPorServicoCsv() {

        List<Demanda> demandas = criarDemandas();

        when(demandaRepository.findFiltradasByServico(1L, "abc"))
                .thenReturn(demandas);

        when(demandaExporter.exportar(demandas))
                .thenReturn("csv");

        String resultado =
                demandaService.exportarDemandasFiltradasPorServicoCsv(
                        1L,
                        "abc"
                );

        assertEquals("csv", resultado);

        verify(auditoriaFacade)
                .exportacaoCsvRealizadaDemanda(
                        "Demandas da servico 1 exportadas"
                );
    }

    @Test
    void deveExportarDemandasPorSolicitanteCsv() {

        List<Demanda> demandas = criarDemandas();

        when(demandaRepository.findByServicoSolicitanteOrderByUsuarioNome(1L))
                .thenReturn(demandas);

        when(demandaExporter.exportar(demandas))
                .thenReturn("csv");

        String resultado =
                demandaService.exportarDemandasPorServicoSolicitanteCsv(1L);

        assertEquals("csv", resultado);

        verify(auditoriaFacade)
                .exportacaoCsvRealizadaDemanda(
                        "Demandas da servico 1 exportadas"
                );
    }

    @Test
    void deveExportarDemandasFiltradasPorSolicitanteCsv() {

        List<Demanda> demandas = criarDemandas();

        when(demandaRepository.findFiltradasByServicoSolicitante(
                1L,
                "abc"))
                .thenReturn(demandas);

        when(demandaExporter.exportar(demandas))
                .thenReturn("csv");

        String resultado =
                demandaService.exportarDemandasFiltradasPorServicoSolicitanteCsv(
                        1L,
                        "abc"
                );

        assertEquals("csv", resultado);

        verify(auditoriaFacade)
                .exportacaoCsvRealizadaDemanda(
                        "Demandas da servico 1 exportadas"
                );
    }


     */

    @Test
    void deveCriarDemandaComServicoSolicitante() {

        Servico servicoSolicitante = new Servico();
        servicoSolicitante.setId(1L);
        servicoSolicitante.setNome("UBS Solicitante");

        Servico servicoResponsavel = new Servico();
        servicoResponsavel.setId(2L);
        servicoResponsavel.setNome("UBS Responsável");

        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setNomeCompleto("Lucas");
        usuario.setServico(servicoResponsavel);

        Servidor servidor = new Servidor();
        servidor.setId(20L);
        servidor.setNome("Servidor");
        servidor.setServico(servicoSolicitante);

        DemandaDTO dto = new DemandaDTO();
        dto.setUsuarioId(10L);
        dto.setMotivoBuscaAtiva(MotivoBuscaAtiva.OUTRO);
        dto.setPrazoDemanda(PrazoDemanda.D1);

        when(usuarioRepository.findById(10L))
                .thenReturn(Optional.of(usuario));

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.save(any(Demanda.class)))
                .thenAnswer(invocation -> {
                    Demanda d = invocation.getArgument(0);
                    d.setId(100L);
                    return d;
                });

        DemandaResponseDTO response =
                demandaService.criar(dto);

        assertEquals(1L, response.getServicoSolicitanteId());
        assertEquals("UBS Solicitante", response.getServicoSolicitanteNome());

        verify(auditoriaFacade)
                .demandaCriada(100L, 10L);
    }

    @ParameterizedTest
    @CsvSource({
            "D1,1",
            "D2,2",
            "D3,3",
            "D7,7",
            "D15,15",
            "D20,20",
            "D30,30"
    })
    void deveCalcularDataLimite(
            PrazoDemanda prazo,
            long dias) {

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("UBS");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Lucas");
        usuario.setServico(servico);

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Servidor");

        DemandaDTO dto = new DemandaDTO();
        dto.setUsuarioId(1L);
        dto.setPrazoDemanda(prazo);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(demandaRepository.save(any(Demanda.class)))
                .thenAnswer(invocation -> {
                    Demanda d = invocation.getArgument(0);
                    d.setId(1L);
                    return d;
                });

        DemandaResponseDTO response =
                demandaService.criar(dto);

        assertEquals(
                response.getDataHoraCriacao().plusDays(dias),
                response.getDataHoraLimite()
        );
    }


    /*
    @Test
    void deveListarDemandasFiltradasPorServicoSolicitante() {

        Long servicoId = 1L;
        String filtro = "João";
        Pageable pageable = PageRequest.of(0, 10);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("João");

        Servidor servidor = new Servidor();
        servidor.setId(2L);
        servidor.setNome("Maria");

        Servico servico = new Servico();
        servico.setId(10L);
        servico.setNome("UBS Centro");

        Demanda demanda = new Demanda();
        demanda.setId(100L);
        demanda.setUsuario(usuario);
        demanda.setServidorCriador(servidor);
        demanda.setServicoResponsavel(servico);
        demanda.setServicoSolicitante(servico);

        Page<Demanda> page = new PageImpl<>(List.of(demanda));

        when(demandaRepository.findFiltradasByServicoSolicitante(
                servicoId,
                filtro,
                pageable
        )).thenReturn(page);

        Page<DemandaResponseDTO> resultado =
                demandaService.listarPorServicoSolicitanteFiltradas(
                        servicoId,
                        filtro,
                        pageable
                );

        assertEquals(1, resultado.getContent().size());

        verify(demandaRepository)
                .findFiltradasByServicoSolicitante(
                        servicoId,
                        filtro,
                        pageable
                );
    }

     */

    @Test
    void deveAceitarComplementoNulo() {
        assertDoesNotThrow(() ->
                demandaService.validarMotivoEComplemento(
                        MotivoBuscaAtiva.OUTRO,
                        null
                )
        );
    }

    @Test
    void deveAceitarMotivoNulo() {
        assertDoesNotThrow(() ->
                demandaService.validarMotivoEComplemento(
                        null,
                        MotivoComplemento.ABANDONO_TRATAMENTO
                )
        );
    }

    @Test
    void deveAceitarComplementoPermitido() {
        assertDoesNotThrow(() ->
                demandaService.validarMotivoEComplemento(
                        MotivoBuscaAtiva.COORDENACAO_CUIDADO,
                        MotivoComplemento.ABANDONO_TRATAMENTO
                )
        );
    }

    @Test
    void deveLancarExcecaoQuandoComplementoNaoPermitido() {
        assertThrows(BusinessException.class, () ->
                demandaService.validarMotivoEComplemento(
                        MotivoBuscaAtiva.BOLSA_FAMILIA,
                        MotivoComplemento.HIV_AIDS
                )
        );
    }

    @Test
    void deveListarMotivos() {
        List<MotivoBuscaResponseDTO> motivos = demandaService.listarMotivos();

        assertNotNull(motivos);
        assertEquals(MotivoBuscaAtiva.values().length, motivos.size());

        for (int i = 0; i < MotivoBuscaAtiva.values().length; i++) {
            MotivoBuscaAtiva motivo = MotivoBuscaAtiva.values()[i];
            MotivoBuscaResponseDTO dto = motivos.get(i);

            assertEquals(motivo.name(), dto.getValor());
            assertEquals(motivo.getDescricao(), dto.getDescricao());
            assertEquals(
                    motivo.getComplementosPermitidos().size(),
                    dto.getComplementos().size()
            );
        }
    }
}