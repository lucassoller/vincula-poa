package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.senha.RecuperarSenhaDTO;
import com.vincula.dto.senha.RedefinirSenhaDTO;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.RecuperacaoSenhaService;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecuperacaoSenhaController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class RecuperacaoSenhaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecuperacaoSenhaService recuperacaoSenhaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuditoriaFacade auditoriaFacade;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveRecuperarSenha() throws Exception {

        RecuperarSenhaDTO dto = new RecuperarSenhaDTO();
        dto.setEmail("teste@teste.com");

        doNothing().when(recuperacaoSenhaService)
                .recuperarSenha(any(RecuperarSenhaDTO.class));

        mockMvc.perform(post("/public/esqueci-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveRedefinirSenha() throws Exception {

        RedefinirSenhaDTO dto = new RedefinirSenhaDTO();
        dto.setToken("token123");
        dto.setNovaSenha("123456");

        doNothing().when(recuperacaoSenhaService)
                .redefinirSenha(any(RedefinirSenhaDTO.class));

        mockMvc.perform(post("/public/redefinir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void naoDeveRecuperarSenhaComDadosInvalidos() throws Exception {

        RecuperarSenhaDTO dto = new RecuperarSenhaDTO();

        mockMvc.perform(post("/public/esqueci-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void naoDeveRedefinirSenhaComDadosInvalidos() throws Exception {

        RedefinirSenhaDTO dto = new RedefinirSenhaDTO();

        mockMvc.perform(post("/public/redefinir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}