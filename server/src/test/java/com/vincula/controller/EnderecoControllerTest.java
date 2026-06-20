package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.endereco.EnderecoResponseDTO;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.EnderecoService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnderecoController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnderecoService enderecoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuditoriaFacade auditoriaFacade;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void deveCriarEndereco() throws Exception {

        EnderecoDTO dto = new EnderecoDTO();
        dto.setRua("Rua A");
        dto.setNumero("123");
        dto.setBairro("Centro");
        dto.setCidade("Porto Alegre");
        dto.setEstado("RS");

        EnderecoResponseDTO response = new EnderecoResponseDTO();

        when(enderecoService.criar(any(EnderecoDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/enderecos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveListarTodos() throws Exception {

        when(enderecoService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/enderecos"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarPorId() throws Exception {

        EnderecoResponseDTO response = new EnderecoResponseDTO();

        when(enderecoService.buscarPorId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/enderecos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveAtualizarEndereco() throws Exception {

        EnderecoDTO dto = new EnderecoDTO();
        dto.setRua("Rua A");
        dto.setNumero("123");
        dto.setBairro("Centro");
        dto.setCidade("Porto Alegre");
        dto.setEstado("RS");

        EnderecoResponseDTO response = new EnderecoResponseDTO();

        when(enderecoService.atualizar(eq(1L), any(EnderecoDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/enderecos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveDeletarEndereco() throws Exception {

        doNothing().when(enderecoService).deletar(1L);

        mockMvc.perform(delete("/enderecos/1"))
                .andExpect(status().isNoContent());
    }

}