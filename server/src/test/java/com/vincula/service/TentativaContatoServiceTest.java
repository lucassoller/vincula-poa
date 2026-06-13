package com.vincula.service;

import com.vincula.dto.tentativaContato.TentativaContatoDTO;
import com.vincula.dto.tentativaContato.TentativaContatoResponseDTO;
import com.vincula.entity.*;
import com.vincula.enums.StatusDemanda;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.TentativaContatoRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TentativaContatoServiceTest {

    @Mock
    private TentativaContatoRepository tentativaRepository;

    @Mock
    private DemandaRepository demandaRepository;

    @Mock
    private ServidorService servidorService;

    @Mock
    private AuditoriaFacade auditoriaFacade;

    @InjectMocks
    private TentativaContatoService tentativaContatoService;

    @Test
    void deveCriarTentativaComSucesso() {

        Demanda demanda = new Demanda();
        demanda.setId(1L);
        demanda.setStatus(StatusDemanda.EM_ANDAMENTO);

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Lucas");

        TentativaContatoDTO dto = new TentativaContatoDTO();
        dto.setDemandaId(1L);

        TentativaContato tentativaSalva = new TentativaContato();
        tentativaSalva.setId(1L);
        tentativaSalva.setDemanda(demanda);
        tentativaSalva.setServidor(servidor);

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(tentativaRepository.existsByDemandaId(1L))
                .thenReturn(true);

        when(tentativaRepository.save(any()))
                .thenReturn(tentativaSalva);

        TentativaContatoResponseDTO response =
                tentativaContatoService.criar(dto);

        assertEquals(1L, response.getId());

        verify(auditoriaFacade)
                .tentativaContatoCriada(1L, 1L);
    }

    @Test
    void deveLancarBusinessExceptionQuandoDemandaFinalizada() {

        Demanda demanda = new Demanda();
        demanda.setId(1L);
        demanda.setStatus(StatusDemanda.FINALIZADA);

        TentativaContatoDTO dto = new TentativaContatoDTO();
        dto.setDemandaId(1L);

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        assertThrows(
                BusinessException.class,
                () -> tentativaContatoService.criar(dto)
        );
    }

    @Test
    void deveAlterarStatusDaDemandaNaPrimeiraTentativa() {

        Demanda demanda = new Demanda();
        demanda.setId(1L);
        demanda.setStatus(StatusDemanda.ABERTA);

        Servidor servidor = new Servidor();
        servidor.setId(1L);

        TentativaContatoDTO dto = new TentativaContatoDTO();
        dto.setDemandaId(1L);

        TentativaContato tentativa = new TentativaContato();
        tentativa.setId(1L);
        tentativa.setDemanda(demanda);
        tentativa.setServidor(servidor);

        when(demandaRepository.findById(1L))
                .thenReturn(Optional.of(demanda));

        when(servidorService.buscarServidorAutenticado())
                .thenReturn(servidor);

        when(tentativaRepository.existsByDemandaId(1L))
                .thenReturn(false);

        when(tentativaRepository.save(any()))
                .thenReturn(tentativa);

        tentativaContatoService.criar(dto);

        assertEquals(
                StatusDemanda.EM_ANDAMENTO,
                demanda.getStatus()
        );

        verify(demandaRepository).save(demanda);

        verify(auditoriaFacade)
                .statusDemandaAlterado(
                        eq(1L),
                        contains("EM_ANDAMENTO")
                );
    }

    @Test
    void deveLancarNotFoundAoAtualizarTentativaInexistente() {

        when(tentativaRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> tentativaContatoService.atualizar(
                        1L,
                        new TentativaContatoDTO()
                )
        );
    }

    @Test
    void deveLancarBusinessExceptionAoAtualizarTentativaDeDemandaFinalizada() {

        Demanda demanda = new Demanda();
        demanda.setStatus(StatusDemanda.FINALIZADA);

        TentativaContato tentativa = new TentativaContato();
        tentativa.setDemanda(demanda);

        when(tentativaRepository.findById(1L))
                .thenReturn(Optional.of(tentativa));

        assertThrows(
                BusinessException.class,
                () -> tentativaContatoService.atualizar(
                        1L,
                        new TentativaContatoDTO()
                )
        );
    }

    @Test
    void deveAtualizarTentativaComSucesso() {

        Demanda demanda = new Demanda();
        demanda.setStatus(StatusDemanda.EM_ANDAMENTO);

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Lucas");

        TentativaContato tentativa = new TentativaContato();
        tentativa.setId(1L);
        tentativa.setDemanda(demanda);
        tentativa.setServidor(servidor);
        tentativa.setDataHora(LocalDateTime.now());

        TentativaContatoDTO dto = new TentativaContatoDTO();
        dto.setDescricao("Nova descrição");

        when(tentativaRepository.findById(1L))
                .thenReturn(Optional.of(tentativa));

        when(tentativaRepository.save(any()))
                .thenReturn(tentativa);

        tentativaContatoService.atualizar(1L, dto);

        verify(tentativaRepository).save(tentativa);

        verify(auditoriaFacade)
                .tentativaContatoAtualizada(
                        eq(1L),
                        anyString()
                );
    }

    @Test
    void deveDeletarTentativaComSucesso() {

        TentativaContato tentativa = new TentativaContato();
        tentativa.setId(1L);

        when(tentativaRepository.findById(1L))
                .thenReturn(Optional.of(tentativa));

        tentativaContatoService.deletar(1L);

        verify(tentativaRepository).delete(tentativa);

        verify(auditoriaFacade)
                .tentativaContatoDeletada(1L);
    }

    @Test
    void deveListarTentativasPorDemanda() {

        Demanda demanda = new Demanda();
        demanda.setId(1L);

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Lucas");

        TentativaContato tentativa = new TentativaContato();
        tentativa.setId(1L);
        tentativa.setDemanda(demanda);
        tentativa.setServidor(servidor);
        tentativa.setDataHora(LocalDateTime.now());

        when(tentativaRepository.findByDemandaId(1L))
                .thenReturn(List.of(tentativa));

        List<TentativaContatoResponseDTO> resultado =
                tentativaContatoService.listarPorDemanda(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void deveListarTentativasPorServidor() {

        Demanda demanda = new Demanda();
        demanda.setId(1L);

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Lucas");

        TentativaContato tentativa = new TentativaContato();
        tentativa.setId(1L);
        tentativa.setDemanda(demanda);
        tentativa.setServidor(servidor);
        tentativa.setDataHora(LocalDateTime.now());

        when(tentativaRepository.findByServidorId(1L))
                .thenReturn(List.of(tentativa));

        List<TentativaContatoResponseDTO> resultado =
                tentativaContatoService.listarPorServidor(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getServidorId());
        assertEquals("Lucas", resultado.get(0).getServidorNome());
    }

    @Test
    void deveListarTodasTentativas() {

        Pageable pageable = PageRequest.of(0, 10);

        Demanda demanda = new Demanda();
        demanda.setId(1L);

        Servidor servidor = new Servidor();
        servidor.setId(1L);
        servidor.setNome("Lucas");

        TentativaContato tentativa = new TentativaContato();
        tentativa.setId(1L);
        tentativa.setDemanda(demanda);
        tentativa.setServidor(servidor);
        tentativa.setDataHora(LocalDateTime.now());

        Page<TentativaContato> page =
                new PageImpl<>(List.of(tentativa));

        when(tentativaRepository.findAllByOrderByDemandaIdAsc(pageable))
                .thenReturn(page);

        Page<TentativaContatoResponseDTO> resultado =
                tentativaContatoService.listarTodas(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(1L,
                resultado.getContent().get(0).getDemandaId());
    }
}