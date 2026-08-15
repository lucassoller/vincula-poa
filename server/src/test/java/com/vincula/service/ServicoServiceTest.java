package com.vincula.service;

import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.servico.*;
import com.vincula.entity.Endereco;
import com.vincula.entity.Servico;
import com.vincula.enums.TipoServico;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
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

    @Test
    void deveListarTodasServicosUbs() {

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("UBS Centro");
        servico.setCnes("1234567");
        servico.setTipoServico(TipoServico.UBS);

        when(servicoRepository.findAllByTipoServicoOrderByNomeAsc(TipoServico.UBS))
                .thenReturn(List.of(servico));

        List<ServicoShortResponseDTO> resultado =
                servicoService.listarTodasServicos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        ServicoShortResponseDTO dto = resultado.get(0);

        assertEquals(1L, dto.getId());
        assertEquals("UBS Centro", dto.getNome());
        assertEquals("1234567", dto.getCnes());

        verify(servicoRepository)
                .findAllByTipoServicoOrderByNomeAsc(TipoServico.UBS);
    }

    @Test
    void deveListarTodosServicos() {

        Servico servico1 = new Servico();
        servico1.setId(1L);
        servico1.setNome("UBS Centro");
        servico1.setCnes("1234567");
        servico1.setTipoServico(TipoServico.UBS);

        Servico servico2 = new Servico();
        servico2.setId(2L);
        servico2.setNome("Farmácia");
        servico2.setCnes("7654321");
        servico2.setTipoServico(TipoServico.OUTRO);

        when(servicoRepository.findAllByOrderByTipoServicoAndNomeAsc())
                .thenReturn(List.of(servico1, servico2));

        List<ServicoShortResponseDTO> resultado =
                servicoService.listarTodosServicos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(1L, resultado.get(0).getId());
        assertEquals("UBS Centro", resultado.get(0).getNome());
        assertEquals("1234567", resultado.get(0).getCnes());

        assertEquals(2L, resultado.get(1).getId());
        assertEquals("Farmácia", resultado.get(1).getNome());
        assertEquals("7654321", resultado.get(1).getCnes());

        verify(servicoRepository)
                .findAllByOrderByTipoServicoAndNomeAsc();
    }

    @Test
    void deveListarTodosServicosFiltrados() {

        FiltroServicoRequestDTO filtro =
                new FiltroServicoRequestDTO();

        Pageable pageable =
                PageRequest.of(0, 10);

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("UBS Centro");
        servico.setCnes("1234567");
        servico.setTipoServico(TipoServico.UBS);

        Page<Servico> pagina =
                new PageImpl<>(
                        List.of(servico),
                        pageable,
                        1
                );

        when(servicoRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(pagina);

        Page<ServicoResponseDTO> resultado =
                servicoService.listarTodosFiltrados(
                        filtro,
                        pageable
                );

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());

        ServicoResponseDTO dto =
                resultado.getContent().get(0);

        assertEquals(1L, dto.getId());
        assertEquals("UBS Centro", dto.getNome());
        assertEquals("1234567", dto.getCnes());

        verify(servicoRepository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void deveListarServicosSeparadosPorTipo() {

        Servico ubs = new Servico();
        ubs.setId(1L);
        ubs.setNome("UBS Centro");
        ubs.setCnes("111");
        ubs.setTipoServico(TipoServico.UBS);

        Servico outro = new Servico();
        outro.setId(2L);
        outro.setNome("Outro Serviço");
        outro.setCnes("222");
        outro.setTipoServico(TipoServico.OUTRO);

        Servico especializado = new Servico();
        especializado.setId(3L);
        especializado.setNome("Serviço Especializado");
        especializado.setCnes("333");
        especializado.setTipoServico(TipoServico.SERVICO_ESPECIALIZADO);

        when(servicoRepository.findAllByOrderByTipoServicoAndNomeAsc())
                .thenReturn(
                        List.of(
                                ubs,
                                outro,
                                especializado
                        )
                );

        ServicosResponseDTO resultado =
                servicoService.listarServicos();

        assertNotNull(resultado);

        assertEquals(3, resultado.getTodos().size());
        assertEquals(1, resultado.getUbs().size());
        assertEquals(2, resultado.getServicos().size());
        assertEquals(1, resultado.getOutros().size());
        assertEquals(1, resultado.getEspecializados().size());

        assertEquals(
                "UBS Centro",
                resultado.getUbs().get(0).getNome()
        );

        assertEquals(
                "Outro Serviço",
                resultado.getOutros().get(0).getNome()
        );

        assertEquals(
                "Serviço Especializado",
                resultado.getEspecializados().get(0).getNome()
        );

        verify(servicoRepository)
                .findAllByOrderByTipoServicoAndNomeAsc();
    }

    @Test
    void deveConverterServicoParaShortDTO() {

        Servico servico = new Servico();
        servico.setId(10L);
        servico.setNome("UBS Centro");
        servico.setCnes("123");

        ServicoShortResponseDTO dto =
                servicoService.toShortDTO(servico);

        assertEquals(10L, dto.getId());
        assertEquals("UBS Centro", dto.getNome());
        assertEquals("123", dto.getCnes());
    }
}