package com.vincula.service;

import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.usuario.*;
import com.vincula.entity.Endereco;
import com.vincula.entity.Servidor;
import com.vincula.entity.Servico;
import com.vincula.entity.Usuario;
import com.vincula.enums.CodigoErro;
import com.vincula.enums.PerfilServidor;
import com.vincula.enums.Sexo;
import com.vincula.exception.BusinessException;
import com.vincula.exception.ConflictException;
import com.vincula.exception.GeorreferenciamentoException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.ServicoRepository;
import com.vincula.repository.UsuarioRepository;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private EnderecoMapper enderecoMapper;

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @Mock
    private TerritorializacaoService territorializacaoService;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private ServidorService servidorService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Servico criarServico(Long id, String nome) {
        Servico servico = new Servico();
        servico.setId(id);
        servico.setNome(nome);
        return servico;
    }

    private Usuario criarUsuario(Long id) {

        Servico servico = criarServico(1L, "UBS Centro");

        Endereco endereco = new Endereco();
        endereco.setLatitude(-30.0346);
        endereco.setLongitude(-51.2177);

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNomeCompleto("Lucas");
        usuario.setDocumento("12345678900");
        usuario.setTelefone("51999999999");
        usuario.setDataNascimento(LocalDate.of(2000, 9, 15));
        usuario.setSexo(Sexo.MASCULINO);
        usuario.setEndereco(endereco);
        usuario.setServico(servico);

        return usuario;
    }

    private UsuarioDTO criarDTO() {

        EnderecoDTO enderecoDTO = new EnderecoDTO();

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNomeCompleto("Lucas");
        dto.setDocumento("12345678900");
        dto.setTelefone("51999999999");
        dto.setDataNascimento(LocalDate.of(2000, 9, 15));
        dto.setSexo(Sexo.MASCULINO);
        dto.setEndereco(enderecoDTO);

        return dto;
    }

    private Servidor criarServidor(PerfilServidor perfil) {

        Servidor servidor = new Servidor();
        servidor.setPerfil(perfil);
        return servidor;
    }

    private void mockGeorreferenciamentoSucesso(
            Endereco endereco,
            Servico servico) {

        doAnswer(invocation -> {

            Endereco e = invocation.getArgument(0);

            e.setLatitude(-30.0346);
            e.setLongitude(-51.2177);

            return null;

        }).when(geocodingService)
                .preencherCoordenadas(endereco);

        when(territorializacaoService.buscarUbsPorCoordenada(
                -30.0346,
                -51.2177
        )).thenReturn(servico);
    }

    @Test
    void deveRetornarUsuarioQuandoBuscarPorId() {

        Usuario usuario = criarUsuario(1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response =
                usuarioService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Lucas", response.getNomeCompleto());
        assertEquals("12345678900", response.getDocumento());
        assertEquals("UBS Centro", response.getServicoNome());
        assertEquals(1L, response.getServicoId());
        assertEquals(Sexo.MASCULINO, response.getSexo());
    }

    @Test
    void deveLancarNotFoundQuandoUsuarioNaoExiste() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> usuarioService.buscarPorId(1L)
        );
    }


    // =========================================================
    // BUSCAR POR DOCUMENTO
    // =========================================================

    @Test
    void deveBuscarUsuarioPorDocumento() {

        Usuario usuario = criarUsuario(1L);

        when(usuarioRepository.findByDocumento("12345678900"))
                .thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response =
                usuarioService.buscarPorDocumento("12345678900");

        assertNotNull(response);
        assertEquals("12345678900", response.getDocumento());
        assertEquals("Lucas", response.getNomeCompleto());
    }

    @Test
    void deveLancarNotFoundQuandoDocumentoNaoExiste() {

        when(usuarioRepository.findByDocumento("123"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> usuarioService.buscarPorDocumento("123")
        );
    }

    @Test
    void deveCriarUsuarioComSucessoPorGeorreferenciamento() {

        UsuarioDTO dto = criarDTO();

        Endereco endereco = new Endereco();

        Servico servico = criarServico(1L, "UBS Centro");

        Servidor servidor =
                criarServidor(PerfilServidor.SERVIDOR_APS);

        when(usuarioRepository.existsByDocumento(dto.getDocumento()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(dto.getEndereco()))
                .thenReturn(endereco);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        mockGeorreferenciamentoSucesso(endereco, servico);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        UsuarioResponseDTO response =
                usuarioService.criar(dto);

        assertNotNull(response);
        assertEquals("Lucas", response.getNomeCompleto());
        assertEquals("12345678900", response.getDocumento());
        assertEquals(1L, response.getServicoId());
        assertEquals("UBS Centro", response.getServicoNome());

        verify(usuarioRepository).save(any(Usuario.class));
        verify(auditoriaFacade).usuarioCriado(any());
        verify(geocodingService).preencherCoordenadas(endereco);
        verify(territorializacaoService)
                .buscarUbsPorCoordenada(-30.0346, -51.2177);
    }

    @Test
    void deveCriarUsuarioInformandoUnidadeSemGeorreferenciar() {

        UsuarioDTO dto = criarDTO();
        dto.setUnidadeSaudeId(10L);

        Endereco endereco = new Endereco();

        Servico servico =
                criarServico(10L, "UBS Zona Sul");

        Servidor servidor =
                criarServidor(PerfilServidor.SERVIDOR_APS);

        when(usuarioRepository.existsByDocumento(dto.getDocumento()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(dto.getEndereco()))
                .thenReturn(endereco);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servicoRepository.findById(10L))
                .thenReturn(Optional.of(servico));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        UsuarioResponseDTO response =
                usuarioService.criar(dto);

        assertEquals(10L, response.getServicoId());
        assertEquals("UBS Zona Sul", response.getServicoNome());

        verify(servicoRepository).findById(10L);
        verify(geocodingService, never())
                .preencherCoordenadas(any());

        verify(territorializacaoService, never())
                .buscarUbsPorCoordenada(anyDouble(), anyDouble());
    }

    @Test
    void deveLancarConflictQuandoCpfJaExisteAoCriar() {

        UsuarioDTO dto = criarDTO();

        when(usuarioRepository.existsByDocumento(dto.getDocumento()))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> usuarioService.criar(dto)
        );

        verify(usuarioRepository, never())
                .save(any());

        verify(enderecoMapper, never())
                .toEntity(any());
    }

    @Test
    void deveLancarBusinessExceptionQuandoDataNascimentoForFuturaAoCriar() {

        UsuarioDTO dto = criarDTO();

        dto.setDataNascimento(
                LocalDate.now().plusDays(1)
        );

        when(usuarioRepository.existsByDocumento(anyString()))
                .thenReturn(false);

        Endereco endereco = new Endereco();

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        assertThrows(
                BusinessException.class,
                () -> usuarioService.criar(dto)
        );

        verify(usuarioRepository, never())
                .save(any());
    }

    @Test
    void deveLancarGeorreferenciamentoNaoEncontradoAoCriar() {

        UsuarioDTO dto = criarDTO();

        Endereco endereco = new Endereco();

        Servidor servidor =
                criarServidor(PerfilServidor.SERVIDOR_APS);

        when(usuarioRepository.existsByDocumento(anyString()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        // Não define latitude/longitude
        doNothing()
                .when(geocodingService)
                .preencherCoordenadas(endereco);

        GeorreferenciamentoException exception =
                assertThrows(
                        GeorreferenciamentoException.class,
                        () -> usuarioService.criar(dto)
                );

        assertEquals(
                CodigoErro.GEORREFERENCIAMENTO_NAO_ENCONTRADO,
                exception.getCodigo()
        );

        verify(usuarioRepository, never())
                .save(any());

        verify(territorializacaoService, never())
                .buscarUbsPorCoordenada(anyDouble(), anyDouble());
    }

    @Test
    void deveLancarTerritorioNaoEncontradoAoCriar() {

        UsuarioDTO dto = criarDTO();

        Endereco endereco = new Endereco();

        Servidor servidor =
                criarServidor(PerfilServidor.SERVIDOR_APS);

        when(usuarioRepository.existsByDocumento(anyString()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        mockGeorreferenciamentoSucesso(
                endereco,
                null
        );

        GeorreferenciamentoException exception =
                assertThrows(
                        GeorreferenciamentoException.class,
                        () -> usuarioService.criar(dto)
                );

        assertEquals(
                CodigoErro.TERRITORIO_NAO_ENCONTRADO,
                exception.getCodigo()
        );

        verify(usuarioRepository, never())
                .save(any());
    }

    @Test
    void deveUsarNaoInformadoQuandoSexoForNuloAoCriar() {

        UsuarioDTO dto = criarDTO();
        dto.setSexo(null);
        dto.setUnidadeSaudeId(1L);

        Endereco endereco = new Endereco();

        Servico servico =
                criarServico(1L, "UBS Centro");

        Servidor servidor =
                criarServidor(PerfilServidor.SERVIDOR_APS);

        when(usuarioRepository.existsByDocumento(anyString()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servicoRepository.findById(1L))
                .thenReturn(Optional.of(servico));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        UsuarioResponseDTO response =
                usuarioService.criar(dto);

        assertEquals(
                Sexo.NAO_INFORMADO,
                response.getSexo()
        );
    }

    @Test
    void deveDefinirServicoSolicitanteQuandoServidorForSolicitante() {

        UsuarioDTO dto = criarDTO();
        dto.setUnidadeSaudeId(20L);

        Endereco endereco = new Endereco();

        Servico servicoSolicitante =
                criarServico(10L, "UBS Solicitante");

        Servico servicoDestino =
                criarServico(20L, "UBS Destino");

        Servidor servidor =
                criarServidor(PerfilServidor.SOLICITANTE);

        servidor.setServico(servicoSolicitante);

        when(usuarioRepository.existsByDocumento(anyString()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servicoRepository.findById(20L))
                .thenReturn(Optional.of(servicoDestino));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        UsuarioResponseDTO response =
                usuarioService.criar(dto);

        assertEquals(
                10L,
                response.getServicoSolicitanteId()
        );

        assertEquals(
                "UBS Solicitante",
                response.getServicoSolicitanteNome()
        );
    }

    @Test
    void naoDeveDefinirServicoSolicitanteParaServidorAps() {

        UsuarioDTO dto = criarDTO();
        dto.setUnidadeSaudeId(20L);

        Endereco endereco = new Endereco();

        Servico servicoDestino =
                criarServico(20L, "UBS Destino");

        Servidor servidor =
                criarServidor(PerfilServidor.SERVIDOR_APS);

        when(usuarioRepository.existsByDocumento(anyString()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servicoRepository.findById(20L))
                .thenReturn(Optional.of(servicoDestino));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        UsuarioResponseDTO response =
                usuarioService.criar(dto);

        assertNull(
                response.getServicoSolicitanteId()
        );

        assertNull(
                response.getServicoSolicitanteNome()
        );
    }

    @Test
    void deveLancarNotFoundQuandoUnidadeInformadaNaoExisteAoCriar() {

        UsuarioDTO dto = criarDTO();
        dto.setUnidadeSaudeId(999L);

        Endereco endereco = new Endereco();

        Servidor servidor =
                criarServidor(PerfilServidor.SERVIDOR_APS);

        when(usuarioRepository.existsByDocumento(anyString()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(servicoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> usuarioService.criar(dto)
        );

        verify(usuarioRepository, never())
                .save(any());
    }

    @Test
    void deveAtualizarUsuarioComSucessoPorGeorreferenciamento() {

        Long id = 1L;

        Usuario usuario = criarUsuario(id);

        UsuarioDTO dto = criarDTO();
        dto.setNomeCompleto("Nome Novo");
        dto.setDocumento("12345678900");
        dto.setUnidadeSaudeId(null);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot(
                dto.getDocumento(),
                id
        )).thenReturn(false);

        doNothing()
                .when(enderecoMapper)
                .updateEntityFromDto(
                        any(),
                        any()
                );

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // Endereço já possui coordenadas
        when(territorializacaoService.buscarUbsPorCoordenada(
                -30.0346,
                -51.2177
        )).thenReturn(usuario.getServico());

        UsuarioResponseDTO response =
                usuarioService.atualizar(id, dto);

        assertNotNull(response);
        assertEquals("Nome Novo", response.getNomeCompleto());
        assertEquals("12345678900", response.getDocumento());

        verify(usuarioRepository)
                .save(usuario);

        verify(auditoriaFacade)
                .usuarioAtualizado(
                        eq(id),
                        anyString()
                );
    }

    @Test
    void deveAtualizarUsuarioComUnidadeInformadaSemGeorreferenciar() {

        Long id = 1L;

        Usuario usuario = criarUsuario(id);

        UsuarioDTO dto = criarDTO();
        dto.setUnidadeSaudeId(20L);

        Servico novaUnidade =
                criarServico(20L, "UBS Nova");

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot(
                dto.getDocumento(),
                id
        )).thenReturn(false);

        when(servicoRepository.findById(20L))
                .thenReturn(Optional.of(novaUnidade));

        doNothing()
                .when(enderecoMapper)
                .updateEntityFromDto(any(), any());

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        UsuarioResponseDTO response =
                usuarioService.atualizar(id, dto);

        assertEquals(
                20L,
                response.getServicoId()
        );

        assertEquals(
                "UBS Nova",
                response.getServicoNome()
        );

        verify(geocodingService, never())
                .preencherCoordenadas(any());

        verify(territorializacaoService, never())
                .buscarUbsPorCoordenada(anyDouble(), anyDouble());
    }

    @Test
    void deveLancarNotFoundAoAtualizarUsuarioInexistente() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> usuarioService.atualizar(
                        1L,
                        criarDTO()
                )
        );

        verify(usuarioRepository, never())
                .save(any());
    }

    @Test
    void deveLancarConflictAoAtualizarCpfDuplicado() {

        Usuario usuario = criarUsuario(1L);

        UsuarioDTO dto = criarDTO();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot(
                dto.getDocumento(),
                1L
        )).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> usuarioService.atualizar(1L, dto)
        );

        verify(usuarioRepository, never())
                .save(any());
    }

    @Test
    void deveLancarBusinessExceptionAoAtualizarDataFutura() {

        Usuario usuario = criarUsuario(1L);

        UsuarioDTO dto = criarDTO();

        dto.setDataNascimento(
                LocalDate.now().plusDays(1)
        );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot(
                anyString(),
                eq(1L)
        )).thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> usuarioService.atualizar(1L, dto)
        );

        verify(usuarioRepository, never())
                .save(any());
    }

    @Test
    void deveLancarGeorreferenciamentoNaoEncontradoAoAtualizar() {

        Usuario usuario = criarUsuario(1L);

        Endereco endereco = new Endereco();

        usuario.setEndereco(endereco);

        UsuarioDTO dto = criarDTO();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot(
                anyString(),
                eq(1L)
        )).thenReturn(false);

        doNothing()
                .when(enderecoMapper)
                .updateEntityFromDto(any(), any());

        doNothing()
                .when(geocodingService)
                .preencherCoordenadas(endereco);

        GeorreferenciamentoException exception =
                assertThrows(
                        GeorreferenciamentoException.class,
                        () -> usuarioService.atualizar(1L, dto)
                );

        assertEquals(
                CodigoErro.GEORREFERENCIAMENTO_NAO_ENCONTRADO,
                exception.getCodigo()
        );

        verify(usuarioRepository, never())
                .save(any());
    }

    @Test
    void deveLancarTerritorioNaoEncontradoAoAtualizar() {

        Usuario usuario = criarUsuario(1L);

        UsuarioDTO dto = criarDTO();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot(
                anyString(),
                eq(1L)
        )).thenReturn(false);

        doNothing()
                .when(enderecoMapper)
                .updateEntityFromDto(any(), any());

        when(territorializacaoService.buscarUbsPorCoordenada(
                -30.0346,
                -51.2177
        )).thenReturn(null);

        GeorreferenciamentoException exception =
                assertThrows(
                        GeorreferenciamentoException.class,
                        () -> usuarioService.atualizar(1L, dto)
                );

        assertEquals(
                CodigoErro.TERRITORIO_NAO_ENCONTRADO,
                exception.getCodigo()
        );

        verify(usuarioRepository, never())
                .save(any());
    }

    @Test
    void deveLancarNotFoundQuandoUnidadeNaoExisteAoAtualizar() {

        Usuario usuario = criarUsuario(1L);

        UsuarioDTO dto = criarDTO();
        dto.setUnidadeSaudeId(999L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot(
                anyString(),
                eq(1L)
        )).thenReturn(false);

        when(servicoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> usuarioService.atualizar(1L, dto)
        );

        verify(usuarioRepository, never())
                .save(any());
    }

    @Test
    void deveUsarNaoInformadoQuandoSexoForNuloAoAtualizar() {

        Usuario usuario = criarUsuario(1L);

        UsuarioDTO dto = criarDTO();
        dto.setSexo(null);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot(
                anyString(),
                eq(1L)
        )).thenReturn(false);

        doNothing()
                .when(enderecoMapper)
                .updateEntityFromDto(any(), any());

        when(territorializacaoService.buscarUbsPorCoordenada(
                -30.0346,
                -51.2177
        )).thenReturn(usuario.getServico());

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        UsuarioResponseDTO response =
                usuarioService.atualizar(1L, dto);

        assertEquals(
                Sexo.NAO_INFORMADO,
                response.getSexo()
        );
    }

    @Test
    void deveDeletarUsuarioComSucesso() {

        Usuario usuario = criarUsuario(1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        usuarioService.deletar(1L);

        verify(usuarioRepository)
                .delete(usuario);

        verify(auditoriaFacade)
                .usuarioDeletado(1L);
    }
    @Test
    void deveLancarNotFoundAoDeletarUsuarioInexistente() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> usuarioService.deletar(1L)
        );

        verify(usuarioRepository, never())
                .delete(any(Usuario.class));

        verify(auditoriaFacade, never())
                .usuarioDeletado(anyLong());
    }

    @Test
    void deveListarTodosUsuarios() {

        Usuario usuario = criarUsuario(1L);

        when(usuarioRepository.findAllByOrderByNomeCompletoAsc())
                .thenReturn(List.of(usuario));

        List<UsuarioShortResponseDTO> resultado =
                usuarioService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals(
                "Lucas",
                resultado.get(0).getNomeCompleto()
        );

        assertEquals(
                "12345678900",
                resultado.get(0).getDocumento()
        );

        assertEquals(
                1L,
                resultado.get(0).getServicoId()
        );

        assertEquals(
                "UBS Centro",
                resultado.get(0).getServicoNome()
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremUsuarios() {

        when(usuarioRepository.findAllByOrderByNomeCompletoAsc())
                .thenReturn(List.of());

        List<UsuarioShortResponseDTO> resultado =
                usuarioService.listarTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }


    @Test
    void deveFiltrarUsuariosPorNomeOuDocumento() {

        Usuario usuario = criarUsuario(1L);

        when(usuarioRepository.buscarPorNomeOuDocumento("Lucas"))
                .thenReturn(List.of(usuario));

        List<UsuarioShortResponseDTO> resultado =
                usuarioService
                        .listarTodosFiltradosPorNomeOuDocumento("Lucas");

        assertEquals(1, resultado.size());
        assertEquals(
                "Lucas",
                resultado.get(0).getNomeCompleto()
        );
    }

    @Test
    void deveRetornarListaVaziaNaBuscaPorNomeOuDocumento() {

        when(usuarioRepository.buscarPorNomeOuDocumento("inexistente"))
                .thenReturn(List.of());

        List<UsuarioShortResponseDTO> resultado =
                usuarioService
                        .listarTodosFiltradosPorNomeOuDocumento(
                                "inexistente"
                        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveListarUsuariosNoAutocomplete() {

        Usuario usuario = criarUsuario(1L);

        AutocompleteUsuarioRequestDTO filtro =
                new AutocompleteUsuarioRequestDTO();

        Pageable pageable =
                PageRequest.of(
                        0,
                        10,
                        Sort.by("nomeCompleto")
                );

        Page<Usuario> page =
                new PageImpl<>(
                        List.of(usuario),
                        pageable,
                        1
                );

        when(usuarioRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        List<UsuarioShortResponseDTO> resultado =
                usuarioService
                        .listarTodosFiltradosPorNomeCompleto(filtro);

        assertEquals(1, resultado.size());
        assertEquals(
                "Lucas",
                resultado.get(0).getNomeCompleto()
        );

        verify(usuarioRepository).findAll(
                any(Specification.class),
                argThat((Pageable p) ->
                        p.getPageNumber() == 0 &&
                                p.getPageSize() == 10 &&
                                p.getSort().getOrderFor("nomeCompleto") != null
                )
        );
    }

    @Test
    void deveListarTodosFiltradosComPaginacao() {

        FiltroUsuarioRequestDTO filtro =
                new FiltroUsuarioRequestDTO();

        Pageable pageable =
                PageRequest.of(
                        1,
                        5,
                        Sort.by("id")
                );

        Usuario usuario = criarUsuario(1L);

        Page<Usuario> page =
                new PageImpl<>(
                        List.of(usuario),
                        PageRequest.of(
                                1,
                                5
                        ),
                        6
                );

        when(usuarioRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        Page<UsuarioResponseDTO> resultado =
                usuarioService.listarTodosFiltrados(
                        filtro,
                        pageable
                );

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals(6, resultado.getTotalElements());

        assertEquals(
                "Lucas",
                resultado.getContent()
                        .get(0)
                        .getNomeCompleto()
        );

        verify(usuarioRepository).findAll(
                any(Specification.class),
                argThat((Pageable p) ->
                        p.getPageNumber() == 1 &&
                                p.getPageSize() == 5 &&
                                p.getSort()
                                        .getOrderFor("nomeCompleto") != null
                )
        );
    }

    @Test
    void deveMapearServicoSolicitanteNoResponseDTO() {

        Usuario usuario = criarUsuario(1L);

        Servico solicitante =
                criarServico(
                        10L,
                        "UBS Solicitante"
                );

        usuario.setServicoSolicitante(solicitante);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response =
                usuarioService.buscarPorId(1L);

        assertEquals(
                10L,
                response.getServicoSolicitanteId()
        );

        assertEquals(
                "UBS Solicitante",
                response.getServicoSolicitanteNome()
        );
    }

    @Test
    void deveMapearServicoSolicitanteNoShortDTO() {

        Usuario usuario = criarUsuario(1L);

        Servico solicitante =
                criarServico(
                        10L,
                        "UBS Solicitante"
                );

        usuario.setServicoSolicitante(solicitante);

        when(usuarioRepository
                .findAllByOrderByNomeCompletoAsc())
                .thenReturn(List.of(usuario));

        List<UsuarioShortResponseDTO> resultado =
                usuarioService.listarTodos();

        assertEquals(
                10L,
                resultado.get(0)
                        .getServicoSolicitanteId()
        );

        assertEquals(
                "UBS Solicitante",
                resultado.get(0)
                        .getServicoSolicitanteNome()
        );
    }

    @Test
    void naoDeveMapearServicoSolicitanteQuandoForNulo() {

        Usuario usuario = criarUsuario(1L);

        usuario.setServicoSolicitante(null);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response =
                usuarioService.buscarPorId(1L);

        assertNull(
                response.getServicoSolicitanteId()
        );

        assertNull(
                response.getServicoSolicitanteNome()
        );
    }
}