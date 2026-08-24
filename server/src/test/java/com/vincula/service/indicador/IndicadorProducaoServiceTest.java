package com.vincula.service.indicador;

import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.IndicadorValorDTO;
import com.vincula.dto.projection.StatusQuantidadeProjection;
import com.vincula.repository.DemandaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndicadorProducaoServiceTest {

    @Mock
    private DemandaRepository demandaRepository;

    @InjectMocks
    private IndicadorProducaoService indicadorProducaoService;

    @Test
    void deveGerarIndicadoresComTodosOsStatus() {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();

        filtro.setServicoResponsavelId(1L);
        filtro.setServicoSolicitanteId(2L);
        filtro.setDataInicial(LocalDate.of(2026, 1, 1));
        filtro.setDataFinal(LocalDate.of(2026, 1, 31));

        StatusQuantidadeProjection aberta = mock(StatusQuantidadeProjection.class);
        when(aberta.getStatus()).thenReturn("ABERTA");
        when(aberta.getQuantidade()).thenReturn(5);

        StatusQuantidadeProjection andamento = mock(StatusQuantidadeProjection.class);
        when(andamento.getStatus()).thenReturn("EM_ANDAMENTO");
        when(andamento.getQuantidade()).thenReturn(10);

        StatusQuantidadeProjection finalizada = mock(StatusQuantidadeProjection.class);
        when(finalizada.getStatus()).thenReturn("FINALIZADA");
        when(finalizada.getQuantidade()).thenReturn(15);

        when(demandaRepository.agruparPorStatus(
                1L,
                2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(List.of(
                aberta,
                andamento,
                finalizada
        ));

        when(demandaRepository.countDemandas(
                1L,
                2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(30L);

        List<IndicadorValorDTO> resultado =
                indicadorProducaoService.gerarIndicadores(filtro);

        assertNotNull(resultado);
        assertEquals(4, resultado.size());

        assertEquals(
                "Demandas abertas",
                resultado.get(0).getIndicador()
        );

        assertEquals(
                "Demandas em andamento",
                resultado.get(1).getIndicador()
        );

        assertEquals(
                "Demandas finalizadas",
                resultado.get(2).getIndicador()
        );

        assertEquals(
                "Total de demandas",
                resultado.get(3).getIndicador()
        );

        assertEquals(5, resultado.get(0).getValor());
        assertEquals(10, resultado.get(1).getValor());
        assertEquals(15, resultado.get(2).getValor());
        assertEquals(30L, resultado.get(3).getValor());

        verify(demandaRepository).agruparPorStatus(
                1L,
                2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        verify(demandaRepository).countDemandas(
                1L,
                2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );
    }

    @Test
    void deveRetornarStatusOriginalQuandoNaoPossuirTraducao() {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();

        filtro.setServicoResponsavelId(1L);
        filtro.setServicoSolicitanteId(2L);
        filtro.setDataInicial(LocalDate.of(2026, 1, 1));
        filtro.setDataFinal(LocalDate.of(2026, 1, 31));

        StatusQuantidadeProjection item =
                mock(StatusQuantidadeProjection.class);

        when(item.getStatus()).thenReturn("STATUS_DESCONHECIDO");
        when(item.getQuantidade()).thenReturn(7);

        when(demandaRepository.agruparPorStatus(
                1L,
                2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(List.of(item));

        when(demandaRepository.countDemandas(
                1L,
                2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(7L);

        List<IndicadorValorDTO> resultado =
                indicadorProducaoService.gerarIndicadores(filtro);

        assertEquals(2, resultado.size());

        assertEquals(
                "STATUS_DESCONHECIDO",
                resultado.get(0).getIndicador()
        );

        assertEquals(
                "Total de demandas",
                resultado.get(1).getIndicador()
        );

        assertEquals(7, resultado.get(0).getValor());
        assertEquals(7L, resultado.get(1).getValor());
    }

    @Test
    void deveGerarApenasTotalQuandoNaoHouverStatus() {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();

        filtro.setServicoResponsavelId(1L);
        filtro.setServicoSolicitanteId(2L);
        filtro.setDataInicial(LocalDate.of(2026, 1, 1));
        filtro.setDataFinal(LocalDate.of(2026, 1, 31));

        when(demandaRepository.agruparPorStatus(
                1L,
                2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(List.of());

        when(demandaRepository.countDemandas(
                1L,
                2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        )).thenReturn(0L);

        List<IndicadorValorDTO> resultado =
                indicadorProducaoService.gerarIndicadores(filtro);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        assertEquals(
                "Total de demandas",
                resultado.get(0).getIndicador()
        );

        assertEquals(
                0L,
                resultado.get(0).getValor()
        );

        verify(demandaRepository).agruparPorStatus(
                1L,
                2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );

        verify(demandaRepository).countDemandas(
                1L,
                2L,
                filtro.getDataInicial(),
                filtro.getDataFinal()
        );
    }
}