package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.MotivoQuantidadeDTO;
import com.vincula.dto.projection.MotivoQuantidadeProjection;
import com.vincula.repository.DemandaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorMotivoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorMotivoService indicadorMotivoService;

    @Test
    void deveGerarIndicadoresComFiltros() {

        FiltroIndicadorRequestDTO filtro =
                new FiltroIndicadorRequestDTO();

        filtro.setServicoResponsavelId(1L);
        filtro.setServicoSolicitanteId(2L);

        MotivoQuantidadeProjection item =
                mock(MotivoQuantidadeProjection.class);

        when(item.getMotivo()).thenReturn("BOLSA_FAMILIA");
        when(item.getQuantidade()).thenReturn(10L);

        when(demandaRepository.listarPrincipaisMotivos(
                1L,
                2L,
                null,
                null
        )).thenReturn(List.of(item));

        List<MotivoQuantidadeDTO> resultado =
                indicadorMotivoService.gerarIndicadores(filtro);

        assertEquals(1, resultado.size());
        assertEquals("Bolsa Família", resultado.get(0).getMotivo());
        assertEquals(10L, resultado.get(0).getQuantidade());

        verify(demandaRepository).listarPrincipaisMotivos(
                1L,
                2L,
                null,
                null
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverMotivos() {

        FiltroIndicadorRequestDTO filtro =
                new FiltroIndicadorRequestDTO();

        when(demandaRepository.listarPrincipaisMotivos(
                null,
                null,
                null,
                null
        )).thenReturn(List.of());

        List<MotivoQuantidadeDTO> resultado =
                indicadorMotivoService.gerarIndicadores(filtro);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(demandaRepository).listarPrincipaisMotivos(
                null,
                null,
                null,
                null
        );
    }

    @ParameterizedTest
    @MethodSource("motivos")
    void deveTraduzirTodosOsMotivos(
            String original,
            String esperado) {

        FiltroIndicadorRequestDTO filtro =
                new FiltroIndicadorRequestDTO();

        MotivoQuantidadeProjection item =
                mock(MotivoQuantidadeProjection.class);

        when(item.getMotivo()).thenReturn(original);
        when(item.getQuantidade()).thenReturn(1L);

        when(demandaRepository.listarPrincipaisMotivos(
                null,
                null,
                null,
                null
        )).thenReturn(List.of(item));

        List<MotivoQuantidadeDTO> resultado =
                indicadorMotivoService.gerarIndicadores(filtro);

        assertEquals(esperado, resultado.get(0).getMotivo());
        assertEquals(1L, resultado.get(0).getQuantidade());
    }

    @Test
    void deveManterMotivoQuandoNaoPossuirTraducao() {

        FiltroIndicadorRequestDTO filtro =
                new FiltroIndicadorRequestDTO();

        MotivoQuantidadeProjection item =
                mock(MotivoQuantidadeProjection.class);

        when(item.getMotivo())
                .thenReturn("MOTIVO_DESCONHECIDO");

        when(item.getQuantidade())
                .thenReturn(5L);

        when(demandaRepository.listarPrincipaisMotivos(
                null,
                null,
                null,
                null
        )).thenReturn(List.of(item));

        List<MotivoQuantidadeDTO> resultado =
                indicadorMotivoService.gerarIndicadores(filtro);

        assertEquals(
                "MOTIVO_DESCONHECIDO",
                resultado.get(0).getMotivo()
        );

        assertEquals(
                5L,
                resultado.get(0).getQuantidade()
        );
    }

    private static Stream<Arguments> motivos() {
        return Stream.of(
                Arguments.of(
                        "COORDENACAO_CUIDADO",
                        "Coordenação do Cuidado"
                ),
                Arguments.of(
                        "BOLSA_FAMILIA",
                        "Bolsa Família"
                ),
                Arguments.of(
                        "SAUDE_MULHER",
                        "Saúde da Mulher"
                ),
                Arguments.of(
                        "SAUDE_CRIANCA",
                        "Saúde da Criança"
                ),
                Arguments.of(
                        "SAUDE_IDOSO",
                        "Saúde do Idoso"
                ),
                Arguments.of(
                        "VACINACAO",
                        "Vacinação"
                ),
                Arguments.of(
                        "DOENCA_CRONICA",
                        "Doença Crônica"
                ),
                Arguments.of(
                        "DOENCA_TRANSMISSIVEL",
                        "Doença Transmissível"
                ),
                Arguments.of(
                        "VIOLENCIA_MORTALIDADE",
                        "Violência e Mortalidade"
                ),
                Arguments.of(
                        "SAUDE_TRABALHADOS",
                        "Saúde do Trabalhador"
                ),
                Arguments.of(
                        "OUTRO",
                        "Outro"
                )
        );
    }
}