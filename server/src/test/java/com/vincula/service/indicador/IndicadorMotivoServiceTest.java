package com.vincula.service.indicador;

import com.vincula.dto.indicador.MotivoQuantidadeDTO;
import com.vincula.dto.projection.MotivoQuantidadeProjection;
import com.vincula.repository.DemandaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorMotivoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorMotivoService service;

    @Test
    void deveTraduzirMotivoOutro() {

        MotivoQuantidadeProjection projection =
                mock(MotivoQuantidadeProjection.class);

        when(projection.getMotivo()).thenReturn("OUTRO");
        when(projection.getQuantidade()).thenReturn(10L);

        when(demandaRepository.listarPrincipaisMotivos())
                .thenReturn(List.of(projection));

        List<MotivoQuantidadeDTO> resultado =
                service.principaisMotivos();

        assertEquals("Outro", resultado.get(0).getMotivo());
        assertEquals(10L, resultado.get(0).getQuantidade());
    }

    @ParameterizedTest
    @CsvSource({
            "COORDENACAO_CUIDADO,Coordenação do Cuidado",
            "BOLSA_FAMILIA,Bolsa Família",
            "SAUDE_MULHER,Saúde da Mulher",
            "SAUDE_CRIANCA,Saúde da Criança",
            "SAUDE_IDOSO,Saúde do Idoso",
            "VACINACAO,Vacinação",
            "DOENCA_CRONICA,Doença Crônica",
            "DOENCA_TRANSMITIVEL,Doença Transmissível",
            "VIOLENCIA_MORTALIDADE,Violência e Mortalidade",
            "OUTRO,Outro",
    })
    void deveTraduzirMotivos(
            String motivoBanco,
            String motivoEsperado) {

        MotivoQuantidadeProjection projection =
                mock(MotivoQuantidadeProjection.class);

        when(projection.getMotivo()).thenReturn(motivoBanco);
        when(projection.getQuantidade()).thenReturn(1L);

        when(demandaRepository.listarPrincipaisMotivos())
                .thenReturn(List.of(projection));

        List<MotivoQuantidadeDTO> resultado =
                service.principaisMotivos();

        assertEquals(
                motivoEsperado,
                resultado.get(0).getMotivo()
        );
    }

    @Test
    void deveListarMotivosPorUnidade() {

        when(demandaRepository
                .listarPrincipaisMotivosPorUnidade(1L))
                .thenReturn(List.of());

        service.principaisMotivosPorUnidade(1L);

        verify(demandaRepository)
                .listarPrincipaisMotivosPorUnidade(1L);
    }

    @Test
    void deveListarMotivosPorPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .listarPrincipaisMotivosPorPeriodo(inicio, fim))
                .thenReturn(List.of());

        service.principaisMotivosPorPeriodo(inicio, fim);

        verify(demandaRepository)
                .listarPrincipaisMotivosPorPeriodo(inicio, fim);
    }

    @Test
    void deveListarMotivosPorUnidadeEPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .listarPrincipaisMotivosPorUnidadeEPeriodo(
                        1L,
                        inicio,
                        fim))
                .thenReturn(List.of());

        service.principaisMotivosPorUnidadeEPeriodo(
                1L,
                inicio,
                fim);

        verify(demandaRepository)
                .listarPrincipaisMotivosPorUnidadeEPeriodo(
                        1L,
                        inicio,
                        fim);
    }

    @Test
    void deveListarMotivosPorServidorEPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .listarPrincipaisMotivosPorUnidadeSolicitanteEPeriodo(
                        1L,
                        inicio,
                        fim))
                .thenReturn(List.of());

        service.principaisMotivosPorServidorEPeriodo(
                1L,
                inicio,
                fim);

        verify(demandaRepository)
                .listarPrincipaisMotivosPorUnidadeSolicitanteEPeriodo(
                        1L,
                        inicio,
                        fim);
    }

    @Test
    void deveListarMotivosPorServidor() {

        when(demandaRepository
                .listarPrincipaisMotivosPorUnidadeSolicitante(1L))
                .thenReturn(List.of());

        service.principaisMotivosPorServidor(1L);

        verify(demandaRepository)
                .listarPrincipaisMotivosPorUnidadeSolicitante(1L);
    }
}