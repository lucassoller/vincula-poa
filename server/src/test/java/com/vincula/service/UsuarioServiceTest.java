package com.vincula.service;

import com.vincula.dto.usuario.UsuarioDTO;
import com.vincula.dto.usuario.UsuarioResponseDTO;
import com.vincula.dto.usuario.UsuarioShortResponseDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.Usuario;
import com.vincula.exception.BusinessException;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.UsuarioRepository;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EnderecoMapper enderecoMapper;

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @Mock
    private TerritorializacaoService territorializacaoService;

    @Mock
    private GeocodingService geocodingService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveLancarNotFoundQuandoUsuarioNaoExiste() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> usuarioService.buscarPorId(1L)
        );
    }

    @Test
    void deveRetornarUsuarioQuandoBuscarPorId() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Lucas");
        usuario.setDocumento("123");
        usuario.setUnidadeSaude(unidade);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response =
                usuarioService.buscarPorId(1L);

        assertEquals(1L, response.getId());
        assertEquals("Lucas", response.getNomeCompleto());
    }

    @Test
    void deveLancarConflictExceptionQuandoCpfJaExiste() {

        UsuarioDTO dto = new UsuarioDTO();
        dto.setDocumento("12345678900");

        when(usuarioRepository.existsByDocumento("12345678900"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> usuarioService.criar(dto)
        );

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarBusinessExceptionQuandoDataNascimentoForFutura() {

        UsuarioDTO dto = new UsuarioDTO();
        dto.setDocumento("12345678900");
        dto.setDataNascimento(LocalDate.now().plusDays(1));

        Endereco endereco = new Endereco();

        when(usuarioRepository.existsByDocumento(anyString()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        assertThrows(
                BusinessException.class,
                () -> usuarioService.criar(dto)
        );

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarNotFoundAoDeletarUsuarioInexistente() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> usuarioService.deletar(1L)
        );

        verify(usuarioRepository, never()).delete(any());
    }

    @Test
    void deveDeletarUsuarioComSucesso() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        usuarioService.deletar(1L);

        verify(usuarioRepository).delete(usuario);
        verify(auditoriaFacade).usuarioDeletado(1L);
    }

    @Test
    void deveLancarNotFoundQuandoBuscarPorDocumentoInexistente() {

        when(usuarioRepository.findByDocumento("123"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> usuarioService.buscarPorDocumento("123")
        );
    }

    @Test
    void deveBuscarUsuarioPorDocumento() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setDocumento("123");
        usuario.setNomeCompleto("Lucas");
        usuario.setUnidadeSaude(unidade);

        when(usuarioRepository.findByDocumento("123"))
                .thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response =
                usuarioService.buscarPorDocumento("123");

        assertEquals("123", response.getDocumento());
    }

    @Test
    void deveCriarUsuarioComSucesso() {

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNomeCompleto("Lucas");
        dto.setDocumento("12345678900");

        Endereco endereco = new Endereco();

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Lucas");
        usuario.setDocumento("12345678900");
        usuario.setEndereco(endereco);
        usuario.setUnidadeSaude(unidade);

        when(usuarioRepository.existsByDocumento(dto.getDocumento()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        doAnswer(invocation -> {
            Endereco e = invocation.getArgument(0);
            e.setLatitude(-30.0);
            e.setLongitude(-51.0);
            return null;
        }).when(geocodingService).preencherCoordenadas(any());

        when(territorializacaoService.buscarUbsPorCoordenada(any(), any()))
                .thenReturn(unidade);

        when(usuarioRepository.save(any()))
                .thenReturn(usuario);

        UsuarioResponseDTO response = usuarioService.criar(dto);

        assertEquals("Lucas", response.getNomeCompleto());

        verify(usuarioRepository).save(any());
        verify(auditoriaFacade).usuarioCriado(any());
    }

    @Test
    void deveLancarBusinessExceptionQuandoGeocodingNaoEncontrarCoordenadas() {

        UsuarioDTO dto = new UsuarioDTO();
        dto.setDocumento("123");

        Endereco endereco = new Endereco();

        when(usuarioRepository.existsByDocumento(any()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        assertThrows(
                BusinessException.class,
                () -> usuarioService.criar(dto)
        );

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarBusinessExceptionQuandoNaoEncontrarUbs() {

        UsuarioDTO dto = new UsuarioDTO();
        dto.setDocumento("123");

        Endereco endereco = new Endereco();

        when(usuarioRepository.existsByDocumento(any()))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        doAnswer(invocation -> {
            Endereco e = invocation.getArgument(0);
            e.setLatitude(-30.0);
            e.setLongitude(-51.0);
            return null;
        }).when(geocodingService).preencherCoordenadas(any());

        when(territorializacaoService.buscarUbsPorCoordenada(any(), any()))
                .thenReturn(null);

        assertThrows(
                BusinessException.class,
                () -> usuarioService.criar(dto)
        );

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarConflictExceptionAoAtualizarCpfDuplicado() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setDocumento("123");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot("123", 1L))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> usuarioService.atualizar(1L, dto)
        );
    }

    @Test
    void deveLancarBusinessExceptionAoAtualizarDataFutura() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setDocumento("123");
        dto.setDataNascimento(LocalDate.now().plusDays(1));

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot(any(), anyLong()))
                .thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> usuarioService.atualizar(1L, dto)
        );
    }

    @Test
    void deveListarTodosUsuarios() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Lucas");
        usuario.setDocumento("123");
        usuario.setUnidadeSaude(unidade);

        when(usuarioRepository.findAllByOrderByNomeCompletoAsc())
                .thenReturn(List.of(usuario));

        List<UsuarioShortResponseDTO> resultado =
                usuarioService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Lucas", resultado.get(0).getNomeCompleto());
    }

    @Test
    void deveListarTodosPaginado() {

        Pageable pageable = PageRequest.of(0, 10);

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Lucas");
        usuario.setDocumento("123");
        usuario.setUnidadeSaude(unidade);

        Page<Usuario> page =
                new PageImpl<>(List.of(usuario));

        when(usuarioRepository.findAllByOrderByNomeCompletoAsc(pageable))
                .thenReturn(page);

        Page<UsuarioResponseDTO> resultado =
                usuarioService.listarTodos(pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveListarUsuariosPorUnidade() {

        Pageable pageable = PageRequest.of(0, 10);

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        Usuario usuario = new Usuario();
        usuario.setUnidadeSaude(unidade);

        Page<Usuario> page =
                new PageImpl<>(List.of(usuario));

        when(usuarioRepository
                .findByUnidadeSaudeIdOrderByNomeCompletoAsc(1L, pageable))
                .thenReturn(page);

        Page<UsuarioResponseDTO> resultado =
                usuarioService.listarTodosPorUnidade(1L, pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveFiltrarUsuariosPorNomeOuDocumento() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Lucas");
        usuario.setDocumento("123");
        usuario.setUnidadeSaude(unidade);

        when(usuarioRepository.buscarPorNomeOuDocumento("Lucas"))
                .thenReturn(List.of(usuario));

        List<UsuarioShortResponseDTO> resultado =
                usuarioService.listarTodosFiltradosPorNomeOuDocumento("Lucas");

        assertEquals(1, resultado.size());
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {

        Long usuarioId = 1L;

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        Endereco endereco = new Endereco();
        endereco.setLatitude(-30.0);
        endereco.setLongitude(-51.0);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNomeCompleto("Nome Antigo");
        usuario.setDocumento("123");
        usuario.setEndereco(endereco);
        usuario.setUnidadeSaude(unidade);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNomeCompleto("Nome Novo");
        dto.setDocumento("123");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));

        when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot("123", usuarioId))
                .thenReturn(false);

        doNothing().when(enderecoMapper)
                .updateEntityFromDto(any(), any());

        doNothing().when(geocodingService)
                .preencherCoordenadas(any());

        when(territorializacaoService.buscarUbsPorCoordenada(anyDouble(), anyDouble()))
                .thenReturn(unidade);

        when(usuarioRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO response =
                usuarioService.atualizar(usuarioId, dto);

        assertNotNull(response);
        assertEquals("Nome Novo", response.getNomeCompleto());

        verify(usuarioRepository).save(usuario);
        verify(auditoriaFacade).usuarioAtualizado(eq(usuarioId), anyString());
    }

    @Test
    void deveLancarNotFoundAoAtualizarUsuarioInexistente() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        UsuarioDTO dto = new UsuarioDTO();

        assertThrows(
                NotFoundException.class,
                () -> usuarioService.atualizar(1L, dto)
        );
    }

    @Test
    void deveLancarBusinessExceptionQuandoNaoEncontrarUbsAoAtualizar() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);

        Endereco endereco = new Endereco();
        endereco.setLatitude(-30.0);
        endereco.setLongitude(-51.0);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setDocumento("123");
        usuario.setEndereco(endereco);
        usuario.setUnidadeSaude(unidade);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setDocumento("123");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot("123", 1L))
                .thenReturn(false);

        when(territorializacaoService.buscarUbsPorCoordenada(anyDouble(), anyDouble()))
                .thenReturn(null);

        assertThrows(
                BusinessException.class,
                () -> usuarioService.atualizar(1L, dto)
        );
    }

    @Test
    void deveLancarBusinessExceptionQuandoGeocodingNaoRetornarCoordenadasAoAtualizar() {

        Endereco endereco = new Endereco();

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setDocumento("123");
        usuario.setEndereco(endereco);
        usuario.setUnidadeSaude(unidade);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setDocumento("123");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.existsByDocumentoAndIdNot("123", 1L))
                .thenReturn(false);

        doNothing().when(enderecoMapper)
                .updateEntityFromDto(any(), any());

        doNothing().when(geocodingService)
                .preencherCoordenadas(any());

        assertThrows(
                BusinessException.class,
                () -> usuarioService.atualizar(1L, dto)
        );

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveListarUsuariosFiltrados() {

        Pageable pageable = PageRequest.of(0, 10);

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Lucas");
        usuario.setDocumento("123");
        usuario.setUnidadeSaude(unidade);

        Page<Usuario> page =
                new PageImpl<>(List.of(usuario));

        when(usuarioRepository.findFiltrados("Lucas", pageable))
                .thenReturn(page);

        Page<UsuarioResponseDTO> resultado =
                usuarioService.listarTodosFiltrados("Lucas", pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Lucas",
                resultado.getContent().get(0).getNomeCompleto());
    }

    @Test
    void deveListarUsuariosFiltradosPorUnidade() {

        Pageable pageable = PageRequest.of(0, 10);

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("Lucas");
        usuario.setDocumento("123");
        usuario.setUnidadeSaude(unidade);

        Page<Usuario> page =
                new PageImpl<>(List.of(usuario));

        when(usuarioRepository.findFiltradosByUnidade(
                1L,
                "Lucas",
                pageable))
                .thenReturn(page);

        Page<UsuarioResponseDTO> resultado =
                usuarioService.listarTodosPorUnidadeFiltrados(
                        1L,
                        "Lucas",
                        pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Lucas",
                resultado.getContent().get(0).getNomeCompleto());
    }
}