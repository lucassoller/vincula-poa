package com.vincula.service;

import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.unidadeSaude.UnidadeSaudeDTO;
import com.vincula.dto.unidadeSaude.UnidadeSaudeResponseDTO;
import com.vincula.dto.unidadeSaude.UnidadeSaudeShortResponseDTO;
import com.vincula.dto.usuario.UsuarioResponseDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.Usuario;
import com.vincula.enums.TipoServico;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class UnidadeSaudeServiceTest {

    @Mock
    private UnidadeSaudeRepository unidadeSaudeRepository;

    @Mock
    private EnderecoMapper enderecoMapper;

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @InjectMocks
    private UnidadeSaudeService unidadeSaudeService;

    @Test
    void deveCriarUnidadeComSucesso() {

        EnderecoDTO dtoEnd = new EnderecoDTO();
        dtoEnd.setRua("Rua A");
        dtoEnd.setNumero("10");
        dtoEnd.setBairro("Centro");
        dtoEnd.setCidade("POA");
        dtoEnd.setEstado("RS");
        dtoEnd.setComplemento("apto 1");

        UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
        dto.setNome("UBS Centro");
        dto.setCnes("1234567");
        dto.setEndereco(dtoEnd);

        Endereco endereco = new Endereco();

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");
        unidade.setCnes("1234567");
        unidade.setEndereco(endereco);

        when(unidadeSaudeRepository.existsByCnes("1234567"))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        when(unidadeSaudeRepository.save(any()))
                .thenReturn(unidade);

        UnidadeSaudeResponseDTO response =
                unidadeSaudeService.criar(dto);

        assertEquals("UBS Centro", response.getNome());

        verify(auditoriaFacade).unidadeSaudeCriada(1L);
        verify(enderecoMapper).toEntity(any());
    }

    @Test
    void deveLancarConflictExceptionQuandoCnesJaExiste() {

        UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
        dto.setCnes("1234567");

        when(unidadeSaudeRepository.existsByCnes("1234567"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> unidadeSaudeService.criar(dto)
        );

        verify(unidadeSaudeRepository, never()).save(any());
    }

    @Test
    void deveLancarNotFoundQuandoBuscarPorIdInexistente() {

        when(unidadeSaudeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> unidadeSaudeService.buscarPorId(1L)
        );
    }

    @Test
    void deveBuscarUnidadePorId() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");

        when(unidadeSaudeRepository.findById(1L))
                .thenReturn(Optional.of(unidade));

        UnidadeSaudeResponseDTO response =
                unidadeSaudeService.buscarPorId(1L);

        assertEquals(1L, response.getId());
    }

    @Test
    void deveLancarNotFoundQuandoBuscarPorCnesInexistente() {

        when(unidadeSaudeRepository.findByCnes("123"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> unidadeSaudeService.buscarPorCnes("123")
        );
    }

    @Test
    void deveAtualizarUnidadeComSucesso() {

        Endereco endereco = new Endereco();

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("Antiga");
        unidade.setCnes("111");
        unidade.setEndereco(endereco);

        UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
        dto.setNome("Nova");
        dto.setCnes("111");

        when(unidadeSaudeRepository.findById(1L))
                .thenReturn(Optional.of(unidade));

        when(unidadeSaudeRepository.existsByCnesAndIdNot("111", 1L))
                .thenReturn(false);

        when(unidadeSaudeRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UnidadeSaudeResponseDTO response =
                unidadeSaudeService.atualizar(1L, dto);

        assertEquals("Nova", response.getNome());

        verify(auditoriaFacade)
                .unidadeSaudeAtualizada(eq(1L), anyString());
    }

    @Test
    void deveLancarConflictExceptionAoAtualizarCnesDuplicado() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);

        UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
        dto.setCnes("123");

        when(unidadeSaudeRepository.findById(1L))
                .thenReturn(Optional.of(unidade));

        when(unidadeSaudeRepository.existsByCnesAndIdNot("123", 1L))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> unidadeSaudeService.atualizar(1L, dto)
        );
    }

    @Test
    void deveDeletarUnidadeComSucesso() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);

        when(unidadeSaudeRepository.findById(1L))
                .thenReturn(Optional.of(unidade));

        unidadeSaudeService.deletar(1L);

        verify(unidadeSaudeRepository).delete(unidade);
        verify(auditoriaFacade).unidadeSaudeDeletada(1L);
    }

    /*
    @Test
    void deveListarTodasUnidades() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");
        unidade.setCnes("123");

        when(unidadeSaudeRepository.findAllByOrderByTipoServicoAndNomeAsc())
                .thenReturn(List.of(unidade));

        List<UnidadeSaudeShortResponseDTO> resultado =
                unidadeSaudeService.listarTodos();

        assertEquals(1, resultado.size());
    }


     */


    @Test
    void deveBuscarUnidadePorCnes() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setNome("UBS Centro");
        unidade.setCnes("1234567");
        unidade.setEndereco(new Endereco());

        when(unidadeSaudeRepository.findByCnes("1234567"))
                .thenReturn(Optional.of(unidade));

        UnidadeSaudeResponseDTO response =
                unidadeSaudeService.buscarPorCnes("1234567");

        assertEquals("1234567", response.getCnes());
    }

    @Test
    void deveListarTodasUbs() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setTipoServico(TipoServico.UBS);
        unidade.setNome("UBS Centro");

        when(unidadeSaudeRepository.findAllByTipoServicoOrderByNomeAsc(TipoServico.UBS))
                .thenReturn(List.of(unidade));

        List<UnidadeSaudeShortResponseDTO> resultado =
                unidadeSaudeService.listarTodosPorServico(TipoServico.UBS);

        assertEquals(1, resultado.size());

        verify(unidadeSaudeRepository)
                .findAllByTipoServicoOrderByNomeAsc(TipoServico.UBS);
    }

    @Test
    void deveListarTodosOutros() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setTipoServico(TipoServico.OUTRO);
        unidade.setNome("Hospital X");

        when(unidadeSaudeRepository.findAllByTipoServicoOrderByNomeAsc(TipoServico.OUTRO))
                .thenReturn(List.of(unidade));

        List<UnidadeSaudeShortResponseDTO> resultado =
                unidadeSaudeService.listarTodosPorServico(TipoServico.OUTRO);

        assertEquals(1, resultado.size());

        verify(unidadeSaudeRepository)
                .findAllByTipoServicoOrderByNomeAsc(TipoServico.OUTRO);
    }

    @Test
    void deveListarTodosOutros2() {

        UnidadeSaude unidade = new UnidadeSaude();
        unidade.setId(1L);
        unidade.setTipoServico(TipoServico.OUTRO);
        unidade.setNome("Hospital X");

        when(unidadeSaudeRepository.findAllByTipoServicoNotOrderByNomeAsc(TipoServico.UBS))
                .thenReturn(List.of(unidade));

        List<UnidadeSaudeShortResponseDTO> resultado =
                unidadeSaudeService.listarTodosOutros();

        assertEquals(1, resultado.size());

        verify(unidadeSaudeRepository)
                .findAllByTipoServicoNotOrderByNomeAsc(TipoServico.UBS);
    }
}