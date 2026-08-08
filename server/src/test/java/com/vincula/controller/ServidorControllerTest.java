package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.senha.MudancaSenhaDTO;
import com.vincula.dto.servidor.MeuPerfilDTO;
import com.vincula.dto.servidor.ServidorDTO;
import com.vincula.dto.servidor.ServidorResponseDTO;
import com.vincula.dto.servidor.TransferirServidorDTO;
import com.vincula.enums.PerfilServidor;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.ServidorService;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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

@WebMvcTest(ServidorController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class ServidorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServidorService servidorService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuditoriaFacade auditoriaFacade;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveCriarServidor() throws Exception {

        ServidorDTO dto = new ServidorDTO();
        dto.setNome("Lucas");
        dto.setEmail("email@gmail.com");
        dto.setLogin("lucas");
        dto.setSenha("senha123");
        dto.setConfirmarSenha("senha123");
        dto.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.criar(any()))
                .thenReturn(new ServidorResponseDTO());

        mockMvc.perform(post("/servidores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveBuscarPorId() throws Exception {

        when(servidorService.buscarPorId(1L))
                .thenReturn(new ServidorResponseDTO());

        mockMvc.perform(get("/servidores/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarPorEmail() throws Exception {

        when(servidorService.buscarPorEmail("a@a.com"))
                .thenReturn(new ServidorResponseDTO());

        mockMvc.perform(get("/servidores/email/a@a.com"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarPorLogin() throws Exception {

        when(servidorService.buscarPorLogin("user"))
                .thenReturn(new ServidorResponseDTO());

        mockMvc.perform(get("/servidores/login/user"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarServidorLogado() throws Exception {

        when(servidorService.getServidorAutenticadoDTO())
                .thenReturn(new ServidorResponseDTO());

        mockMvc.perform(get("/servidores/me"))
                .andExpect(status().isOk());
    }

    @Test
    void deveAtualizarServidor() throws Exception {

        ServidorDTO dto = new ServidorDTO();
        dto.setNome("Lucas");
        dto.setEmail("email@gmail.com");
        dto.setLogin("lucas");
        dto.setSenha("senha123");
        dto.setConfirmarSenha("senha123");
        dto.setPerfil(PerfilServidor.GESTAO_MUNICIPAL);

        when(servidorService.atualizar(eq(1L), any()))
                .thenReturn(new ServidorResponseDTO());

        mockMvc.perform(put("/servidores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveTransferirServidor() throws Exception {

        TransferirServidorDTO dto = new TransferirServidorDTO();
        dto.setServicoId(1L);
        dto.setPerfil(PerfilServidor.SERVIDOR_APS);

        when(servidorService.transferirServidor(eq(1L), any()))
                .thenReturn(new ServidorResponseDTO());

        mockMvc.perform(put("/servidores/transferir/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveAtualizarMeuPerfil() throws Exception {

        MeuPerfilDTO dto = new MeuPerfilDTO();
        dto.setLogin("login");
        dto.setEmail("email@gmail.com");
        dto.setNome("nome");

        when(servidorService.atualizarMeuPerfil(any()))
                .thenReturn(new ServidorResponseDTO());

        mockMvc.perform(put("/servidores/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveAlterarMinhaSenha() throws Exception {

        MudancaSenhaDTO dto = new MudancaSenhaDTO();
        dto.setSenhaAtual("senha123");
        dto.setNovaSenha("senha");
        dto.setConfirmarSenha("senha");

        when(servidorService.atualizarMinhaSenha(any()))
                .thenReturn(new ServidorResponseDTO());

        mockMvc.perform(put("/servidores/me/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveAlterarSenhaServidor() throws Exception {

        MudancaSenhaDTO dto = new MudancaSenhaDTO();
        dto.setSenhaAtual("senha123");
        dto.setNovaSenha("senha");
        dto.setConfirmarSenha("senha");

        doNothing().when(servidorService).alterarSenha(eq(1L), any());

        mockMvc.perform(put("/servidores/servidores/1/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveDeletarServidor() throws Exception {

        doNothing().when(servidorService).deletar(1L);

        mockMvc.perform(delete("/servidores/1"))
                .andExpect(status().isNoContent());
    }
}