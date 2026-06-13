package com.vincula.service;

import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.endereco.EnderecoResponseDTO;
import com.vincula.entity.Endereco;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.EnderecoRepository;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @InjectMocks
    private EnderecoService enderecoService;

    @Test
    void deveCriarEndereco() {

        EnderecoDTO dto = new EnderecoDTO();
        dto.setRua("Rua A");

        Endereco salvo = new Endereco();
        salvo.setId(1L);
        salvo.setRua("Rua A");

        when(enderecoRepository.save(any(Endereco.class)))
                .thenReturn(salvo);

        EnderecoResponseDTO response = enderecoService.criar(dto);

        assertEquals(1L, response.getId());

        verify(auditoriaFacade)
                .enderecoCriado(1L);
    }

    @Test
    void deveListarTodos() {

        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setRua("Rua A");

        when(enderecoRepository.findAll())
                .thenReturn(List.of(endereco));

        List<EnderecoResponseDTO> lista =
                enderecoService.listarTodos();

        assertEquals(1, lista.size());
        assertEquals("Rua A", lista.get(0).getRua());
    }

    @Test
    void deveBuscarPorId() {

        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setRua("Rua A");

        when(enderecoRepository.findById(1L))
                .thenReturn(Optional.of(endereco));

        EnderecoResponseDTO response =
                enderecoService.buscarPorId(1L);

        assertEquals(1L, response.getId());
    }

    @Test
    void deveLancarExcecaoAoBuscarPorIdInexistente() {

        when(enderecoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> enderecoService.buscarPorId(1L)
        );
    }

    @Test
    void deveAtualizarEndereco() {

        Endereco endereco = new Endereco();
        endereco.setId(1L);

        EnderecoDTO dto = new EnderecoDTO();
        dto.setRua("Rua Nova");

        when(enderecoRepository.findById(1L))
                .thenReturn(Optional.of(endereco));

        when(enderecoRepository.save(any(Endereco.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EnderecoResponseDTO response =
                enderecoService.atualizar(1L, dto);

        assertEquals("Rua Nova", response.getRua());

        verify(auditoriaFacade)
                .enderecoAtualizado(eq(1L), anyString());
    }

    @Test
    void deveLancarExcecaoAoAtualizarEnderecoInexistente() {

        EnderecoDTO dto = new EnderecoDTO();

        when(enderecoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> enderecoService.atualizar(1L, dto)
        );
    }

    @Test
    void deveDeletarEndereco() {

        Endereco endereco = new Endereco();
        endereco.setId(1L);

        when(enderecoRepository.findById(1L))
                .thenReturn(Optional.of(endereco));

        enderecoService.deletar(1L);

        verify(enderecoRepository)
                .delete(endereco);

        verify(auditoriaFacade)
                .enderecoDeletado(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarEnderecoInexistente() {

        when(enderecoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> enderecoService.deletar(1L)
        );
    }

    @Test
    void deveMapearTodosOsCampos() {

        Endereco endereco = new Endereco();
        endereco.setId(1L);
        endereco.setRua("Rua");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("POA");
        endereco.setComplemento("Ap 1");
        endereco.setEstado("RS");

        when(enderecoRepository.findById(1L))
                .thenReturn(Optional.of(endereco));

        EnderecoResponseDTO dto =
                enderecoService.buscarPorId(1L);

        assertEquals("Rua", dto.getRua());
        assertEquals("123", dto.getNumero());
        assertEquals("Centro", dto.getBairro());
        assertEquals("POA", dto.getCidade());
        assertEquals("Ap 1", dto.getComplemento());
        assertEquals("RS", dto.getEstado());
    }
}