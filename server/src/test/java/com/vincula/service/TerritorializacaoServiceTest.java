package com.vincula.service;

import com.vincula.entity.TerritorioUbs;
import com.vincula.entity.Servico;
import com.vincula.repository.TerritorioUbsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TerritorializacaoServiceTest {
    @Mock
    private TerritorioUbsRepository repository;

    @InjectMocks
    private TerritorializacaoService territorializacaoService;

    @Test
    void deveRetornarUbsQuandoCoordenadaEstiverDentroDoTerritorio() {

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setNome("UBS Centro");

        TerritorioUbs territorio = new TerritorioUbs();
        territorio.setServico(servico);

        territorio.setGeojson("""
        {
          "type":"Polygon",
          "coordinates":[[
            [-51.0,-30.0],
            [-51.0,-29.0],
            [-50.0,-29.0],
            [-50.0,-30.0],
            [-51.0,-30.0]
          ]]
        }
        """);

        when(repository.findAll())
                .thenReturn(List.of(territorio));

        Servico resultado =
                territorializacaoService.buscarUbsPorCoordenada(
                        -29.5,
                        -50.5
                );

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveRetornarNullQuandoCoordenadaEstiverForaDosTerritorios() {

        Servico servico = new Servico();

        TerritorioUbs territorio = new TerritorioUbs();
        territorio.setServico(servico);

        territorio.setGeojson("""
        {
          "type":"Polygon",
          "coordinates":[[
            [-51.0,-30.0],
            [-51.0,-29.0],
            [-50.0,-29.0],
            [-50.0,-30.0],
            [-51.0,-30.0]
          ]]
        }
        """);

        when(repository.findAll())
                .thenReturn(List.of(territorio));

        Servico resultado =
                territorializacaoService.buscarUbsPorCoordenada(
                        -32.0,
                        -55.0
                );

        assertNull(resultado);
    }

    @Test
    void deveIgnorarGeoJsonInvalido() {

        TerritorioUbs territorio = new TerritorioUbs();
        territorio.setGeojson("geojson invalido");

        when(repository.findAll())
                .thenReturn(List.of(territorio));

        Servico resultado =
                territorializacaoService.buscarUbsPorCoordenada(
                        -30.0,
                        -51.0
                );

        assertNull(resultado);
    }

    @Test
    void deveRetornarPrimeiroTerritorioEncontrado() {

        Servico servico1 = new Servico();
        servico1.setId(1L);

        Servico servico2 = new Servico();
        servico2.setId(2L);

        String geojson = """
        {
          "type":"Polygon",
          "coordinates":[[
            [-51.0,-30.0],
            [-51.0,-29.0],
            [-50.0,-29.0],
            [-50.0,-30.0],
            [-51.0,-30.0]
          ]]
        }
        """;

        TerritorioUbs t1 = new TerritorioUbs();
        t1.setGeojson(geojson);
        t1.setServico(servico1);

        TerritorioUbs t2 = new TerritorioUbs();
        t2.setGeojson(geojson);
        t2.setServico(servico2);

        when(repository.findAll())
                .thenReturn(List.of(t1, t2));

        Servico resultado =
                territorializacaoService.buscarUbsPorCoordenada(
                        -29.5,
                        -50.5
                );

        assertEquals(1L, resultado.getId());
    }
}