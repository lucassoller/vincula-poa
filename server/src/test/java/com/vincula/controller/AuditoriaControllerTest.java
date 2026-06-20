package com.vincula.controller;

import com.vincula.dto.auditoria.AuditoriaDTO;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.AuditoriaService;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditoriaFacade auditoriaFacade;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuditoriaService auditoriaService;


    @Test
    void deveListarLogs() throws Exception {

        Page<AuditoriaDTO> page = new PageImpl<>(List.of());

        when(auditoriaService.listarTodos(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/auditoria")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(auditoriaService).listarTodos(any(Pageable.class));
    }

    @Test
    void deveListarPorServidor() throws Exception {

        Page<AuditoriaDTO> page = new PageImpl<>(List.of());

        when(auditoriaService.listarPorServidor(any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/auditoria/servidor/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

    }

    @Test
    void deveListarPorPeriodo() throws Exception {

        Page<AuditoriaDTO> page = new PageImpl<>(List.of());

        when(auditoriaService.listarPorPeriodo(
                any(),
                any(),
                any()))
                .thenReturn(page);

        mockMvc.perform(get("/auditoria/periodo")
                        .param("inicio", "2025-01-01T00:00:00")
                        .param("fim", "2025-12-31T23:59:59")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarPorServidorEPeriodo() throws Exception {

        Page<AuditoriaDTO> page = new PageImpl<>(List.of());

        when(auditoriaService.listarPorServidorEPeriodo(
                eq(1L),
                any(),
                any(),
                any()))
                .thenReturn(page);

        mockMvc.perform(get("/auditoria/servidor/1/periodo")
                        .param("inicio", "2025-01-01T00:00:00")
                        .param("fim", "2025-12-31T23:59:59")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

}