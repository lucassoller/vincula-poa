package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.tentativaContato.TentativaContatoDTO;
import com.vincula.dto.tentativaContato.TentativaContatoResponseDTO;
import com.vincula.enums.TipoTentativaContato;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.TentativaContatoService;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TentativaContatoController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class TentativaContatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TentativaContatoService tentativaContatoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuditoriaFacade auditoriaFacade;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveCriarTentativaContato() throws Exception {

        TentativaContatoDTO dto = new TentativaContatoDTO();
        dto.setDemandaId(1L);
        dto.setTipo(TipoTentativaContato.OUTRO);
        dto.setDescricao("descricao");

        TentativaContatoResponseDTO response = new TentativaContatoResponseDTO();

        when(tentativaContatoService.criar(any(TentativaContatoDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/tentativas-contato")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveAtualizarTentativaContato() throws Exception {

        TentativaContatoDTO dto = new TentativaContatoDTO();
        dto.setDemandaId(1L);
        dto.setTipo(TipoTentativaContato.OUTRO);
        dto.setDescricao("descricao");

        TentativaContatoResponseDTO response = new TentativaContatoResponseDTO();

        when(tentativaContatoService.atualizar(eq(1L), any(TentativaContatoDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/tentativas-contato/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarPorDemanda() throws Exception {

        when(tentativaContatoService.listarPorDemanda(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/tentativas-contato/demanda/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarTodas() throws Exception {

        Page<TentativaContatoResponseDTO> page =
                new PageImpl<>(List.of());

        when(tentativaContatoService.listarTodas(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/tentativas-contato")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void deveDeletarTentativaContato() throws Exception {

        doNothing().when(tentativaContatoService).deletar(1L);

        mockMvc.perform(delete("/tentativas-contato/1"))
                .andExpect(status().isNoContent());
    }
}