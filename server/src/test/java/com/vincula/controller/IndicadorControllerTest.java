package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.indicador.FiltroIndicadorRequestDTO;
import com.vincula.dto.indicador.IndicadorDTO;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.indicador.IndicadorService;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndicadorController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class IndicadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IndicadorService indicadorService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuditoriaFacade auditoriaFacade;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveGerarIndicadores() throws Exception {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();

        IndicadorDTO response = new IndicadorDTO();

        when(indicadorService.gerarIndicadores(any(FiltroIndicadorRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/indicadores/geral")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filtro)))
                .andExpect(status().isOk());

        verify(indicadorService)
                .gerarIndicadores(any(FiltroIndicadorRequestDTO.class));
    }

    @Test
    void deveExportarIndicadoresCsv() throws Exception {

        FiltroIndicadorRequestDTO filtro = new FiltroIndicadorRequestDTO();

        String csv = """
                indicador,valor
                Total de demandas,10
                """;

        when(indicadorService.exportarIndicadoresCsv(
                any(FiltroIndicadorRequestDTO.class)))
                .thenReturn(csv);

        mockMvc.perform(post("/indicadores/exportar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filtro)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.valueOf("text/csv")))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        matchesPattern(
                                "attachment; filename=indicadores-vincula-poa\\d{2}-\\d{2}-\\d{4}_\\d{2}-\\d{2}\\.csv"
                        )
                ))
                .andExpect(content().string(csv));

        verify(indicadorService)
                .exportarIndicadoresCsv(any(FiltroIndicadorRequestDTO.class));
    }
}