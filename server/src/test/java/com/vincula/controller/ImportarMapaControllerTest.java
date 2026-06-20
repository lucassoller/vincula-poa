package com.vincula.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.ImportarTerritorioService;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImportarMapaController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class ImportarMapaControllerTest {

    @MockitoBean
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImportarTerritorioService importarTerritorioService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuditoriaFacade auditoriaFacade;

    @Test
    void deveImportarMapa() throws Exception {

        JsonNode node = new ObjectMapper().createObjectNode();

        when(objectMapper.valueToTree(any(Map.class)))
                .thenReturn(node);

        doNothing().when(importarTerritorioService).importar(any(JsonNode.class));

        mockMvc.perform(post("/territorios/importar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "type": "FeatureCollection",
                              "features": []
                            }
                            """))
                .andExpect(status().isNoContent());

        verify(objectMapper).valueToTree(any(Map.class));
        verify(importarTerritorioService).importar(any(JsonNode.class));
    }

    @Test
    void deveExportarMapa() throws Exception {

        when(importarTerritorioService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/territorios/mapa"))
                .andExpect(status().isOk());
    }

}