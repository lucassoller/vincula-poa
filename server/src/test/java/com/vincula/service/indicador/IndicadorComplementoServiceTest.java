package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.MotivoQuantidadeDTO;
import com.vincula.repository.DemandaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import com.vincula.dto.projection.MotivoQuantidadeProjection;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorComplementoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorComplementoService indicadorComplementoService;

    @Test
    void deveGerarIndicadoresComFiltros() {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();
        filtro.setServicoResponsavelId(1L);
        filtro.setServicoSolicitanteId(2L);
        filtro.setDataInicial(null);
        filtro.setDataFinal(null);

        MotivoQuantidadeProjection item = mock(MotivoQuantidadeProjection.class);

        when(item.getMotivo()).thenReturn("GESTANTE");
        when(item.getQuantidade()).thenReturn(10L);

        when(demandaRepository.listarPrincipaisComplementos(
                1L,
                2L,
                null,
                null
        )).thenReturn(List.of(item));

        List<MotivoQuantidadeDTO> resultado =
                indicadorComplementoService.gerarIndicadores(filtro);

        assertEquals(1, resultado.size());
        assertEquals("Gestante", resultado.get(0).getMotivo());
        assertEquals(10L, resultado.get(0).getQuantidade());

        verify(demandaRepository).listarPrincipaisComplementos(
                1L,
                2L,
                null,
                null
        );
    }

    @Test
    void deveRetornarSemComplementoQuandoMotivoForNulo() {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();

        MotivoQuantidadeProjection item = mock(MotivoQuantidadeProjection.class);

        when(item.getMotivo()).thenReturn(null);
        when(item.getQuantidade()).thenReturn(5L);

        when(demandaRepository.listarPrincipaisComplementos(
                null,
                null,
                null,
                null
        )).thenReturn(List.of(item));

        List<MotivoQuantidadeDTO> resultado =
                indicadorComplementoService.gerarIndicadores(filtro);

        assertEquals(1, resultado.size());
        assertEquals("Sem complemento", resultado.get(0).getMotivo());
        assertEquals(5L, resultado.get(0).getQuantidade());
    }

    @Test
    void deveManterComplementoQuandoNaoPossuirTraducao() {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();

        MotivoQuantidadeProjection item = mock(MotivoQuantidadeProjection.class);

        when(item.getMotivo()).thenReturn("COMPLEMENTO_DESCONHECIDO");
        when(item.getQuantidade()).thenReturn(3L);

        when(demandaRepository.listarPrincipaisComplementos(
                null,
                null,
                null,
                null
        )).thenReturn(List.of(item));

        List<MotivoQuantidadeDTO> resultado =
                indicadorComplementoService.gerarIndicadores(filtro);

        assertEquals("COMPLEMENTO_DESCONHECIDO",
                resultado.get(0).getMotivo());

        assertEquals(3L, resultado.get(0).getQuantidade());
    }

    @ParameterizedTest
    @MethodSource("complementos")
    void deveTraduzirTodosOsComplementos(
            String original,
            String esperado) {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();

        MotivoQuantidadeProjection item = mock(MotivoQuantidadeProjection.class);

        when(item.getMotivo()).thenReturn(original);
        when(item.getQuantidade()).thenReturn(1L);

        when(demandaRepository.listarPrincipaisComplementos(
                null,
                null,
                null,
                null
        )).thenReturn(List.of(item));

        List<MotivoQuantidadeDTO> resultado =
                indicadorComplementoService.gerarIndicadores(filtro);

        assertEquals(esperado, resultado.get(0).getMotivo());
        assertEquals(1L, resultado.get(0).getQuantidade());
    }

    private static Stream<Arguments> complementos() {
        return Stream.of(
                Arguments.of("ABANDONO_TRATAMENTO", "Abandono de tratamento"),
                Arguments.of("AVISO_CONSULTA", "Aviso de consulta"),
                Arguments.of("EGRESSO_HOSPITALAR", "Egresso hospitalar"),
                Arguments.of("FALTOSO_CONSULTA", "Faltoso a consulta"),
                Arguments.of("FALTOSO_EXAME", "Faltoso a exame"),
                Arguments.of("FALTOSO_PROCEDIMENTO", "Faltoso a procedimento"),
                Arguments.of("CRIANCA_MENOR", "Criança menor"),
                Arguments.of("DEMAIS_BENEFICIARIOS", "Demais beneficiários"),
                Arguments.of("GESTANTE", "Gestante"),
                Arguments.of("MULHER_IDADE_FERTIL", "Mulher em idade fértil"),
                Arguments.of("VACINACAO_CRIANCA_MENOR", "Vacinação criança menor"),
                Arguments.of("GESTANTE_EXPOSTA", "Gestante exposta"),
                Arguments.of("PRE_NATAL", "Pré-natal"),
                Arguments.of("PUERPERIO", "Puerpério"),
                Arguments.of("RASTREAMENTO_CANCER_COLO_UTERO",
                        "Rastreamento câncer do colo do útero"),
                Arguments.of("RASTREAMENTO_CANCER_MAMA",
                        "Rastreamento câncer de mama"),
                Arguments.of("BAIXO_PESO", "Baixo peso"),
                Arguments.of("BINOMIO", "Binômio"),
                Arguments.of("CRIANCA_EXPOSTA", "Criança exposta"),
                Arguments.of("DESENVOLVIMENTO_INFANTIL",
                        "Desenvolvimento infantil"),
                Arguments.of("PUERICULTURA", "Puericultura"),
                Arguments.of("TRIAGEM_NEONATAL", "Triagem neonatal"),
                Arguments.of("AVALIACAO_MULTIDIMENSIONAL",
                        "Avaliação multidimensional"),
                Arguments.of("DECLINIO_COGNITIVO", "Declínio cognitivo"),
                Arguments.of("POLIFARMACIA", "Polifarmácia"),
                Arguments.of("VISITA_DOMICILIAR", "Visita domiciliar"),
                Arguments.of("VACINACAO", "Vacinação"),
                Arguments.of("ADOLESCENTE", "Adolescente"),
                Arguments.of("ADULTO", "Adulto"),
                Arguments.of("CRIANCA", "Criança"),
                Arguments.of("VACINACAO_GESTANTE", "Vacinação gestante"),
                Arguments.of("IDOSO", "Idoso"),
                Arguments.of("DIABETES", "Diabetes"),
                Arguments.of("DOENCA_FALCIFORME", "Doença falciforme"),
                Arguments.of("HIPERTENSAO_ARTERIAL",
                        "Hipertensão arterial"),
                Arguments.of("OUTROS_AGRAVOS_CRONICOS",
                        "Outros agravos crônicos"),
                Arguments.of("HANSENIASE", "Hanseníase"),
                Arguments.of("HEPATITES_VIRAIS", "Hepatites virais"),
                Arguments.of("HIV_AIDS", "HIV/AIDS"),
                Arguments.of(
                        "OUTRAS_DOENCAS_NOTIFICACAO_COMPULSORIA",
                        "Outras doenças de notificação compulsória"
                ),
                Arguments.of("SIFILIS", "Sífilis"),
                Arguments.of("TUBERCULOSE", "Tuberculose"),
                Arguments.of("MORTALIDADE_INFANTIL",
                        "Mortalidade infantil"),
                Arguments.of("MORTALIDADE_MATERNA",
                        "Mortalidade materna"),
                Arguments.of("TRABALHO_INFANTIL",
                        "Trabalho infantil"),
                Arguments.of("VIOLENCIA_CONTRA_CRIANCAS",
                        "Violência contra crianças"),
                Arguments.of("VIOLENCIA_CONTRA_IDOSOS",
                        "Violência contra idosos"),
                Arguments.of("VIOLENCIA_CONTRA_MULHERES",
                        "Violência contra mulheres"),
                Arguments.of("INTOXICACAO_EXOGENA",
                        "Intoxicação exógena")
        );
    }
}