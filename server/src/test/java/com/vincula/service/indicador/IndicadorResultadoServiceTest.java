package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.dto.projection.DesfechoQuantidadeProjection;
import com.vincula.repository.DemandaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorResultadoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorResultadoService indicadorResultadoService;


    private FiltroIndicadorRequestDTO criarFiltro() {
        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();
        filtro.setServicoResponsavelId(1L);
        filtro.setServicoSolicitanteId(2L);
        filtro.setDataInicial(LocalDate.of(2026, 1, 1));
        filtro.setDataFinal(LocalDate.of(2026, 1, 31));

        return filtro;
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
    void deveTraduzirDesfechos(String desfecho, String esperado) {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho()).thenReturn(desfecho);
        when(projection.getQuantidade()).thenReturn(1L);

        when(demandaRepository.countDemandasFinalizadas(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(1L);

        when(demandaRepository.agruparPorDesfecho(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.gerarIndicadores(filtro);

        assertEquals(1, resultado.size());
        assertEquals(esperado, resultado.get(0).getIndicador());
        assertEquals(100.0, resultado.get(0).getValor());
    }


    @Test
    void deveRetornarDesfechoOriginalQuandoNaoExistirTraducao() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho())
                .thenReturn("DESFECHO_TESTE");

        when(projection.getQuantidade())
                .thenReturn(5L);

        when(demandaRepository.countDemandasFinalizadas(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(10L);

        when(demandaRepository.agruparPorDesfecho(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.gerarIndicadores(filtro);

        assertEquals(1, resultado.size());
        assertEquals(
                "DESFECHO_TESTE",
                resultado.get(0).getIndicador()
        );
        assertEquals(50.0, resultado.get(0).getValor());
    }


    @Test
    void deveRetornarPercentualZeroQuandoNaoExistiremDemandasFinalizadas() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho()).thenReturn("OBITO");
        when(projection.getQuantidade()).thenReturn(10L);

        when(demandaRepository.countDemandasFinalizadas(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(0L);

        when(demandaRepository.agruparPorDesfecho(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.gerarIndicadores(filtro);

        assertEquals(1, resultado.size());
        assertEquals("Óbito", resultado.get(0).getIndicador());
        assertEquals(0.0, resultado.get(0).getValor());
    }


    @Test
    void deveCalcularPercentualCorretamente() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        DesfechoQuantidadeProjection projection =
                mock(DesfechoQuantidadeProjection.class);

        when(projection.getDesfecho())
                .thenReturn("NAO_LOCALIZADO");

        when(projection.getQuantidade())
                .thenReturn(3L);

        when(demandaRepository.countDemandasFinalizadas(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(12L);

        when(demandaRepository.agruparPorDesfecho(
                filtro.getServicoResponsavelId(),
                filtro.getServicoSolicitanteId(),
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(List.of(projection));

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.gerarIndicadores(filtro);

        assertEquals(1, resultado.size());
        assertEquals(
                "Não localizado",
                resultado.get(0).getIndicador()
        );
        assertEquals(25.0, resultado.get(0).getValor());
    }


    @Test
    void deveChamarRepositoryComOsFiltrosCorretos() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        when(demandaRepository.countDemandasFinalizadas(
                1L,
                2L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        )).thenReturn(0L);

        when(demandaRepository.agruparPorDesfecho(
                1L,
                2L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        )).thenReturn(List.of());

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.gerarIndicadores(filtro);

        assertEquals(0, resultado.size());

        verify(demandaRepository).countDemandasFinalizadas(
                1L,
                2L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );

        verify(demandaRepository).agruparPorDesfecho(
                1L,
                2L,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );
    }


    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremDesfechos() {

        FiltroIndicadorRequestDTO filtro = criarFiltro();

        when(demandaRepository.countDemandasFinalizadas(
                any(),
                any(),
                any(),
                any()
        )).thenReturn(0L);

        when(demandaRepository.agruparPorDesfecho(
                any(),
                any(),
                any(),
                any()
        )).thenReturn(List.of());

        List<IndicadorValorDTO> resultado =
                indicadorResultadoService.gerarIndicadores(filtro);

        assertEquals(0, resultado.size());
        assertEquals(List.of(), resultado);
    }
}