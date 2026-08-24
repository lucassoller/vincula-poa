package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.auditoria.AuditoriaDTO;
import com.vincula.dto.auditoria.FiltroAuditoriaRequestDTO;
import com.vincula.enums.TipoAcaoAuditoria;
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

import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveListarAuditoriasFiltradas() throws Exception {

        FiltroAuditoriaRequestDTO filtro = new FiltroAuditoriaRequestDTO();

        AuditoriaDTO auditoria = new AuditoriaDTO(
                1L,
                TipoAcaoAuditoria.LOGIN_REALIZADO,
                "Demanda",
                1L,
                "Demanda criada",
                LocalDateTime.now(),
                1L,
                "Lucas",
                "127.0.0.1"
        );

        Page<AuditoriaDTO> pagina =
                new PageImpl<>(List.of(auditoria));

        when(auditoriaService.listarTodosFiltrados(
                any(FiltroAuditoriaRequestDTO.class),
                any(Pageable.class)))
                .thenReturn(pagina);

        mockMvc.perform(
                        post("/auditoria/filtrados")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(filtro))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON
                ))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(auditoriaService).listarTodosFiltrados(
                any(FiltroAuditoriaRequestDTO.class),
                any(Pageable.class)
        );
    }
}