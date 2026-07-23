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
class IndicadorComplementoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorComplementoService service;

    @Test
    void deveTraduzirComplementoVacinacao() {

        MotivoQuantidadeProjection projection =
                mock(MotivoQuantidadeProjection.class);

        when(projection.getMotivo()).thenReturn("VACINACAO");
        when(projection.getQuantidade()).thenReturn(10L);

        when(demandaRepository.listarPrincipaisComplementos())
                .thenReturn(List.of(projection));

        List<MotivoQuantidadeDTO> resultado =
                service.principaisComplementos();

        assertEquals("Vacinação", resultado.get(0).getMotivo());
        assertEquals(10L, resultado.get(0).getQuantidade());
    }

    @ParameterizedTest
    @CsvSource({
             "ABANDONO_TRATAMENTO,Abandono de tratamento",
             "AVISO_CONSULTA,Aviso de consulta",
             "EGRESSO_HOSPITALAR,Egresso hospitalar",
             "FALTOSO_CONSULTA,Faltoso a consulta",
             "FALTOSO_EXAME,Faltoso a exame",
             "FALTOSO_PROCEDIMENTO,Faltoso a procedimento",
             "CRIANCA_MENOR,Criança menor",
             "DEMAIS_BENEFICIARIOS,Demais beneficiários",
             "GESTANTE,Gestante",
             "MULHER_IDADE_FERTIL,Mulher em idade fértil",
             "VACINACAO_CRIANCA_MENOR,Vacinação criança menor",
             "GESTANTE_EXPOSTA,Gestante exposta",
             "PRE_NATAL,Pré-natal",
             "PUERPERIO,Puerpério",
             "RASTREAMENTO_CANCER_COLO_UTERO,Rastreamento câncer do colo do útero",
             "RASTREAMENTO_CANCER_MAMA,Rastreamento câncer de mama",
             "BAIXO_PESO,Baixo peso",
             "BINOMIO,Binômio",
             "CRIANCA_EXPOSTA,Criança exposta",
             "DESENVOLVIMENTO_INFANTIL,Desenvolvimento infantil",
             "PUERICULTURA,Puericultura",
             "TRIAGEM_NEONATAL,Triagem neonatal",
             "AVALIACAO_MULTIDIMENSIONAL,Avaliação multidimensional",
             "DECLINIO_COGNITIVO,Declínio cognitivo",
             "POLIFARMACIA,Polifarmácia",
             "VISITA_DOMICILIAR,Visita domiciliar",
             "VACINACAO,Vacinação",
             "ADOLESCENTE,Adolescente",
             "ADULTO,Adulto",
             "CRIANCA,Criança",
             "VACINACAO_GESTANTE,Vacinação gestante",
             "IDOSO,Idoso",
             "DIABETES,Diabetes",
             "DOENCA_FALCIFORME,Doença falciforme",
             "HIPERTENSAO_ARTERIAL,Hipertensão arterial",
             "OUTROS_AGRAVOS_CRONICOS,Outros agravos crônicos",
             "HANSENIASE,Hanseníase",
             "HEPATITES_VIRAIS,Hepatites virais",
             "HIV_AIDS,HIV/AIDS",
             "OUTRAS_DOENCAS_NOTIFICACAO_COMPULSORIA,Outras doenças de notificação compulsória",
             "SIFILIS,Sífilis",
             "TUBERCULOSE,Tuberculose",
             "MORTALIDADE_INFANTIL,Mortalidade infantil",
             "MORTALIDADE_MATERNA,Mortalidade materna",
             "TRABALHO_INFANTIL,Trabalho infantil",
             "VIOLENCIA_CONTRA_CRIANCAS,Violência contra crianças",
             "VIOLENCIA_CONTRA_IDOSOS,Violência contra idosos",
             "VIOLENCIA_CONTRA_MULHERES,Violência contra mulheres",
            "default,default"
    })
    void deveTraduzirComplementos(
            String motivoBanco,
            String motivoEsperado) {

        MotivoQuantidadeProjection projection =
                mock(MotivoQuantidadeProjection.class);

        when(projection.getMotivo()).thenReturn(motivoBanco);
        when(projection.getQuantidade()).thenReturn(1L);

        when(demandaRepository.listarPrincipaisComplementos())
                .thenReturn(List.of(projection));

        List<MotivoQuantidadeDTO> resultado =
                service.principaisComplementos();

        assertEquals(
                motivoEsperado,
                resultado.get(0).getMotivo()
        );
    }

    @Test
    void deveListarComplementosPorUnidade() {

        when(demandaRepository
                .listarPrincipaisComplementosPorUnidade(1L))
                .thenReturn(List.of());

        service.principaisComplementosPorUnidade(1L);

        verify(demandaRepository)
                .listarPrincipaisComplementosPorUnidade(1L);
    }

    @Test
    void deveListarComplementosPorPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .listarPrincipaisComplementosPorPeriodo(inicio, fim))
                .thenReturn(List.of());

        service.principaisComplementosPorPeriodo(inicio, fim);

        verify(demandaRepository)
                .listarPrincipaisComplementosPorPeriodo(inicio, fim);
    }

    @Test
    void deveListarComplementosPorUnidadeEPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .listarPrincipaisComplementosPorUnidadeEPeriodo(
                        1L,
                        inicio,
                        fim))
                .thenReturn(List.of());

        service.principaisComplementosPorUnidadeEPeriodo(
                1L,
                inicio,
                fim);

        verify(demandaRepository)
                .listarPrincipaisComplementosPorUnidadeEPeriodo(
                        1L,
                        inicio,
                        fim);
    }

    @Test
    void deveListarComplementosPorServidorEPeriodo() {

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .listarPrincipaisComplementosPorUnidadeSolicitanteEPeriodo(
                        1L,
                        inicio,
                        fim))
                .thenReturn(List.of());

        service.principaisComplementosPorServidorEPeriodo(
                1L,
                inicio,
                fim);

        verify(demandaRepository)
                .listarPrincipaisComplementosPorUnidadeSolicitanteEPeriodo(
                        1L,
                        inicio,
                        fim);
    }

    @Test
    void deveListarComplementosPorServidor() {

        when(demandaRepository
                .listarPrincipaisComplementosPorUnidadeSolicitante(1L))
                .thenReturn(List.of());

        service.principaisComplementosPorServidor(1L);

        verify(demandaRepository)
                .listarPrincipaisComplementosPorUnidadeSolicitante(1L);
    }
}