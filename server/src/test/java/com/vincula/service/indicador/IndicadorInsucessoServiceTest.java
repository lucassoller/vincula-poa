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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorInsucessoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorInsucessoService service;

    @Test
    void deveTraduzirMotivoFaltoso() {

        MotivoQuantidadeProjection projection =
                mock(MotivoQuantidadeProjection.class);

        when(projection.getMotivo()).thenReturn("FALTOSO");
        when(projection.getQuantidade()).thenReturn(10L);

        when(demandaRepository.listarPrincipaisMotivosInsucesso())
                .thenReturn(List.of(projection));

        List<MotivoQuantidadeDTO> resultado =
                service.principaisMotivosInsucesso();

        assertEquals("Faltoso", resultado.get(0).getMotivo());
        assertEquals(10L, resultado.get(0).getQuantidade());
    }

    @ParameterizedTest
    @CsvSource({
            "FALTOSO,Faltoso",
            "ABANDONO,Abandono de tratamento",
            "CONDICAO_SAUDE,Condição de saúde",
            "OUTRO,Outro",
            "QUALQUER,QUALQUER"
    })
    void deveTraduzirMotivos(
            String motivoBanco,
            String motivoEsperado) {

        MotivoQuantidadeProjection projection =
                mock(MotivoQuantidadeProjection.class);

        when(projection.getMotivo()).thenReturn(motivoBanco);
        when(projection.getQuantidade()).thenReturn(1L);

        when(demandaRepository.listarPrincipaisMotivosInsucesso())
                .thenReturn(List.of(projection));

        List<MotivoQuantidadeDTO> resultado =
                service.principaisMotivosInsucesso();

        assertEquals(
                motivoEsperado,
                resultado.get(0).getMotivo()
        );
    }

    @Test
    void deveListarMotivosPorUnidade() {

        when(demandaRepository
                .listarPrincipaisMotivosInsucessoPorUnidade(1L))
                .thenReturn(List.of());

        service.principaisMotivosInsucessoPorUnidade(1L);

        verify(demandaRepository)
                .listarPrincipaisMotivosInsucessoPorUnidade(1L);
    }

    @Test
    void deveListarMotivosPorPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .listarPrincipaisMotivosInsucessoPorPeriodo(inicio, fim))
                .thenReturn(List.of());

        service.principaisMotivosInsucessoPorPeriodo(inicio, fim);

        verify(demandaRepository)
                .listarPrincipaisMotivosInsucessoPorPeriodo(inicio, fim);
    }

    @Test
    void deveListarMotivosPorUnidadeEPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .listarPrincipaisMotivosInsucessoPorUnidadeEPeriodo(
                        1L,
                        inicio,
                        fim))
                .thenReturn(List.of());

        service.principaisMotivosInsucessoPorUnidadeEPeriodo(
                1L,
                inicio,
                fim);

        verify(demandaRepository)
                .listarPrincipaisMotivosInsucessoPorUnidadeEPeriodo(
                        1L,
                        inicio,
                        fim);
    }

    @Test
    void deveListarMotivosPorServidorEPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .listarPrincipaisMotivosInsucessoPorServidorEPeriodo(
                        1L,
                        inicio,
                        fim))
                .thenReturn(List.of());

        service.principaisMotivosInsucessoPorServidorEPeriodo(
                1L,
                inicio,
                fim);

        verify(demandaRepository)
                .listarPrincipaisMotivosInsucessoPorServidorEPeriodo(
                        1L,
                        inicio,
                        fim);
    }

    @Test
    void deveListarMotivosPorServidor() {

        when(demandaRepository
                .listarPrincipaisMotivosInsucessoPorServidor(1L))
                .thenReturn(List.of());

        service.principaisMotivosInsucessoPorServidor(1L);

        verify(demandaRepository)
                .listarPrincipaisMotivosInsucessoPorServidor(1L);
    }

}