package com.vincula.service;

import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.servico.ServicoDTO;
import com.vincula.dto.servico.ServicoResponseDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.Servico;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.ServicoRepository;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class ServicoServiceTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private EnderecoMapper enderecoMapper;

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @InjectMocks
    private ServicoService servicoService;

    @Test
    void deveCriarServicoComSucesso() {

        EnderecoDTO dtoEnd = new EnderecoDTO();
        dtoEnd.setRua("Rua A");
        dtoEnd.setNumero("10");
        dtoEnd.setBairro("Centro");
        dtoEnd.setCidade("POA");
        dtoEnd.setEstado("RS");
        dtoEnd.setComplemento("apto 1");

        ServicoDTO dto = new ServicoDTO();
        dto.setNome("UBS Centro");
        dto.setCnes("1234567");
        dto.setEndereco(dtoEnd);

        Endereco endereco = new Endereco();

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("UBS Centro");
        servico.setCnes("1234567");
        servico.setEndereco(endereco);

        when(servicoRepository.existsByCnes("1234567"))
                .thenReturn(false);

        when(enderecoMapper.toEntity(any()))
                .thenReturn(endereco);

        when(servicoRepository.save(any()))
                .thenReturn(servico);

        ServicoResponseDTO response =
                servicoService.criar(dto);

        assertEquals("UBS Centro", response.getNome());

        verify(auditoriaFacade).servicoCriado(1L);
        verify(enderecoMapper).toEntity(any());
    }

    @Test
    void deveLancarConflictExceptionQuandoCnesJaExiste() {

        ServicoDTO dto = new ServicoDTO();
        dto.setCnes("1234567");

        when(servicoRepository.existsByCnes("1234567"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> servicoService.criar(dto)
        );

        verify(servicoRepository, never()).save(any());
    }

    @Test
    void deveLancarNotFoundQuandoBuscarPorIdInexistente() {

        when(servicoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> servicoService.buscarPorId(1L)
        );
    }

    @Test
    void deveBuscarServicoPorId() {

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("UBS Centro");

        when(servicoRepository.findById(1L))
                .thenReturn(Optional.of(servico));

        ServicoResponseDTO response =
                servicoService.buscarPorId(1L);

        assertEquals(1L, response.getId());
    }

    @Test
    void deveLancarNotFoundQuandoBuscarPorCnesInexistente() {

        when(servicoRepository.findByCnes("123"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> servicoService.buscarPorCnes("123")
        );
    }

    @Test
    void deveAtualizarServicoComSucesso() {

        Endereco endereco = new Endereco();

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("Antiga");
        servico.setCnes("111");
        servico.setEndereco(endereco);

        ServicoDTO dto = new ServicoDTO();
        dto.setNome("Nova");
        dto.setCnes("111");

        when(servicoRepository.findById(1L))
                .thenReturn(Optional.of(servico));

        when(servicoRepository.existsByCnesAndIdNot("111", 1L))
                .thenReturn(false);

        when(servicoRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServicoResponseDTO response =
                servicoService.atualizar(1L, dto);

        assertEquals("Nova", response.getNome());

        verify(auditoriaFacade)
                .servicoAtualizado(eq(1L), anyString());
    }

    @Test
    void deveLancarConflictExceptionAoAtualizarCnesDuplicado() {

        Servico servico = new Servico();
        servico.setId(1L);

        ServicoDTO dto = new ServicoDTO();
        dto.setCnes("123");

        when(servicoRepository.findById(1L))
                .thenReturn(Optional.of(servico));

        when(servicoRepository.existsByCnesAndIdNot("123", 1L))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> servicoService.atualizar(1L, dto)
        );
    }

    @Test
    void deveDeletarServicoComSucesso() {

        Servico servico = new Servico();
        servico.setId(1L);

        when(servicoRepository.findById(1L))
                .thenReturn(Optional.of(servico));

        servicoService.deletar(1L);

        verify(servicoRepository).delete(servico);
        verify(auditoriaFacade).servicoDeletado(1L);
    }

    /*
    @Test
    void deveListarTodasServicos() {

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("UBS Centro");
        servico.setCnes("123");

        when(servicoRepository.findAllByOrderByTipoServicoAndNomeAsc())
                .thenReturn(List.of(servico));

        List<ServicoShortResponseDTO> resultado =
                servicoService.listarTodos();

        assertEquals(1, resultado.size());
    }


     */


    @Test
    void deveBuscarServicoPorCnes() {

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("UBS Centro");
        servico.setCnes("1234567");
        servico.setEndereco(new Endereco());

        when(servicoRepository.findByCnes("1234567"))
                .thenReturn(Optional.of(servico));

        ServicoResponseDTO response =
                servicoService.buscarPorCnes("1234567");

        assertEquals("1234567", response.getCnes());
    }
}