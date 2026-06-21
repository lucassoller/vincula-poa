package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void deveBuscarIndicadorGeral() throws Exception {

        when(indicadorService.indicadorGeral(
                any(), any(), any(), any()
        )).thenReturn(new IndicadorDTO());

        mockMvc.perform(get("/indicadores/geral")
                        .param("unidadeSaudeId", "1")
                        .param("inicio", "2025-01-01T00:00:00")
                        .param("fim", "2025-12-31T23:59:59")
                        .param("unidadeSolicitanteId", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarIndicadorGeralSemParametros() throws Exception {

        when(indicadorService.indicadorGeral(
                isNull(), isNull(), isNull(), isNull()
        )).thenReturn(new IndicadorDTO());

        mockMvc.perform(get("/indicadores/geral"))
                .andExpect(status().isOk());
    }

    @Test
    void deveExportarIndicadorCsv() throws Exception {

        when(indicadorService.exportarIndicadorGeralCsv(
                any(), any(), any(), any()
        )).thenReturn("csv,dados");

        mockMvc.perform(get("/indicadores/exportar")
                        .param("unidadeSaudeId", "1")
                        .param("inicio", "2025-01-01T00:00:00")
                        .param("fim", "2025-12-31T23:59:59")
                        .param("unidadeSolicitanteId", "2"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andExpect(content().contentType("text/csv"));
    }
}