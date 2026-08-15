package com.vincula.service;

import com.vincula.entity.Servico;
import com.vincula.repository.ServicoRepository;
import com.vincula.repository.TerritorioUbsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TerritorializacaoServiceTest {

    @Mock
    private TerritorioUbsRepository repository;

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private TerritorializacaoService territorializacaoService;


    @Test
    void deveRetornarUbsQuandoEncontrarPorCoordenada() {

        Double latitude = -30.0346;
        Double longitude = -51.2177;

        Long servicoId = 10L;

        Servico servico = new Servico();
        servico.setId(servicoId);
        servico.setNome("UBS Teste");

        when(repository.buscarServicoIdPorCoordenada(latitude, longitude))
                .thenReturn(Optional.of(servicoId));

        when(servicoRepository.findById(servicoId))
                .thenReturn(Optional.of(servico));

        Servico resultado =
                territorializacaoService.buscarUbsPorCoordenada(
                        latitude,
                        longitude
                );

        assertNotNull(resultado);
        assertEquals(servicoId, resultado.getId());
        assertEquals("UBS Teste", resultado.getNome());

        verify(repository)
                .buscarServicoIdPorCoordenada(latitude, longitude);

        verify(servicoRepository)
                .findById(servicoId);
    }


    @Test
    void deveRetornarNullQuandoServicoNaoForEncontrado() {

        Double latitude = -30.0346;
        Double longitude = -51.2177;

        Long servicoId = 10L;

        when(repository.buscarServicoIdPorCoordenada(latitude, longitude))
                .thenReturn(Optional.of(servicoId));

        when(servicoRepository.findById(servicoId))
                .thenReturn(Optional.empty());

        Servico resultado =
                territorializacaoService.buscarUbsPorCoordenada(
                        latitude,
                        longitude
                );

        assertNull(resultado);

        verify(repository)
                .buscarServicoIdPorCoordenada(latitude, longitude);

        verify(servicoRepository)
                .findById(servicoId);
    }


    @Test
    void deveRetornarNullQuandoNaoEncontrarUbsPorCoordenada() {

        Double latitude = -30.0346;
        Double longitude = -51.2177;

        when(repository.buscarServicoIdPorCoordenada(latitude, longitude))
                .thenReturn(Optional.empty());

        Servico resultado =
                territorializacaoService.buscarUbsPorCoordenada(
                        latitude,
                        longitude
                );

        assertNull(resultado);

        verify(repository)
                .buscarServicoIdPorCoordenada(latitude, longitude);

        verify(servicoRepository, never())
                .findById(anyLong());
    }
}