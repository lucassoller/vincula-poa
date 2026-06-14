package com.vincula.service.indicador;

import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.dto.projection.DesfechoQuantidadeProjection;
import com.vincula.enums.StatusDemanda;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndicadorResultadoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorResultadoService indicadorResultadoService;

    @Test
    void deveRetornarPercentualZeroQuandoNaoExistiremDemandasFinalizadas() {
        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho()).thenReturn("OBITO");
        when(projection.getQuantidade()).thenReturn(10L);

        when(demandaRepository.countByStatus(StatusDemanda.FINALIZADA))
                .thenReturn(0.0);

        when(demandaRepository.agruparPorDesfecho())
                .thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.percentualPorDesfecho();

        assertEquals(0.0, resultado.get(0).getValor());
    }

    @Test
    void deveRetornarDesfechoOriginalQuandoNaoExistirTraducao() {
        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho()).thenReturn("DESFECHO_TESTE");
        when(projection.getQuantidade()).thenReturn(1L);

        when(demandaRepository.countByStatus(StatusDemanda.FINALIZADA))
                .thenReturn(1.0);

        when(demandaRepository.agruparPorDesfecho())
                .thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.percentualPorDesfecho();

        assertEquals("DESFECHO_TESTE", resultado.get(0).getIndicador());
    }

    @ParameterizedTest
    @CsvSource({
            "ENCONTRADO_VINCULADO,Encontrado e vinculado à APS",
            "ENCONTRADO_RECUSOU,Encontrado e recusou atendimento",
            "NAO_LOCALIZADO,Não localizado",
            "ENDERECO_INCORRETO,Endereço incorreto",
            "MUDOU_TERRITORIO,Mudou de território",
            "OBITO,Óbito",
            "OUTRO,Outro"
    })
    void deveTraduzirDesfechos(
            String desfecho,
            String esperado) {

        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho()).thenReturn(desfecho);
        when(projection.getQuantidade()).thenReturn(1L);

        when(demandaRepository.countByStatus(StatusDemanda.FINALIZADA))
                .thenReturn(1.0);

        when(demandaRepository.agruparPorDesfecho())
                .thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.percentualPorDesfecho();

        assertEquals(esperado, resultado.get(0).getIndicador());
    }

    @Test
    void deveRetornarPercentualPorDesfechoPorUnidade() {
        Long unidadeId = 1L;

        when(demandaRepository.countByStatusAndUnidadeResponsavelId(
                StatusDemanda.FINALIZADA,
                unidadeId
        )).thenReturn(10.0);

        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho())
                .thenReturn("OBITO");
        when(projection.getQuantidade())
                .thenReturn(2L);

        when(demandaRepository.agruparPorDesfechoEUnidade(unidadeId))
                .thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.percentualPorDesfechoPorUnidade(unidadeId);

        assertEquals(1, resultado.size());
        assertEquals("Óbito", resultado.get(0).getIndicador());
        assertEquals(20.0, resultado.get(0).getValor());
    }
    @Test
    void deveRetornarPercentualPorDesfechoPorServidor() {
        Long servidorId = 1L;

        when(demandaRepository.countByStatusAndUnidadeSolicitanteId(
                StatusDemanda.FINALIZADA,
                servidorId
        )).thenReturn(10.0);

        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho())
                .thenReturn("NAO_LOCALIZADO");
        when(projection.getQuantidade())
                .thenReturn(3L);

        when(demandaRepository.agruparPorDesfechoEUnidadeSolicitante(servidorId))
                .thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.percentualPorDesfechoPorServidor(servidorId);

        assertEquals("Não localizado", resultado.get(0).getIndicador());
        assertEquals(30.0, resultado.get(0).getValor());
    }

    @Test
    void deveRetornarPercentualPorDesfechoPorPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository.countByStatusAndDataHoraCriacaoBetween(
                StatusDemanda.FINALIZADA,
                inicio,
                fim
        )).thenReturn(5.0);

        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho())
                .thenReturn("OUTRO");
        when(projection.getQuantidade())
                .thenReturn(1L);

        when(demandaRepository.agruparPorDesfechoPorPeriodo(inicio, fim))
                .thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.percentualPorDesfechoPorPeriodo(inicio, fim);

        assertEquals("Outro", resultado.get(0).getIndicador());
        assertEquals(20.0, resultado.get(0).getValor());
    }

    @Test
    void deveRetornarPercentualPorDesfechoPorUnidadeEPeriodo() {
        Long unidadeId = 1L;
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .countByStatusAndUnidadeResponsavelIdAndDataHoraCriacaoBetween(
                        StatusDemanda.FINALIZADA,
                        unidadeId,
                        inicio,
                        fim
                ))
                .thenReturn(4.0);

        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho())
                .thenReturn("MUDOU_TERRITORIO");
        when(projection.getQuantidade())
                .thenReturn(1L);

        when(demandaRepository
                .agruparPorDesfechoEUnidadePorPeriodo(
                        unidadeId,
                        inicio,
                        fim
                ))
                .thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.percentualPorDesfechoPorUnidadeEPeriodo(
                        unidadeId,
                        inicio,
                        fim
                );

        assertEquals("Mudou de território", resultado.get(0).getIndicador());
        assertEquals(25.0, resultado.get(0).getValor());
    }

    @Test
    void deveRetornarPercentualPorDesfechoPorServidorEPeriodo() {
        Long servidorId = 1L;
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now();

        when(demandaRepository
                .countByStatusAndUnidadeSolicitanteIdAndDataHoraCriacaoBetween(
                        StatusDemanda.FINALIZADA,
                        servidorId,
                        inicio,
                        fim
                ))
                .thenReturn(8.0);

        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho())
                .thenReturn("ENDERECO_INCORRETO");
        when(projection.getQuantidade())
                .thenReturn(2L);

        when(demandaRepository
                .agruparPorDesfechoEUnidadeSolicitantePorPeriodo(
                        servidorId,
                        inicio,
                        fim
                ))
                .thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.percentualPorDesfechoPorServidorEPeriodo(
                        servidorId,
                        inicio,
                        fim
                );

        assertEquals("Endereço incorreto", resultado.get(0).getIndicador());
        assertEquals(25.0, resultado.get(0).getValor());
    }


}