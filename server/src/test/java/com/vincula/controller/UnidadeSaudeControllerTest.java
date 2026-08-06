package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.unidadeSaude.UnidadeSaudeDTO;
import com.vincula.dto.unidadeSaude.UnidadeSaudeResponseDTO;
import com.vincula.enums.TipoServico;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.UnidadeSaudeService;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UnidadeSaudeController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class UnidadeSaudeControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditoriaFacade auditoriaFacade;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UnidadeSaudeService unidadeSaudeService;

    @Test
    void deveBuscarPorId() throws Exception {

        UnidadeSaudeResponseDTO dto = new UnidadeSaudeResponseDTO();
        dto.setId(1L);
        dto.setNome("UBS Centro");

        when(unidadeSaudeService.buscarPorId(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/unidades-saude/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("UBS Centro"));

        verify(unidadeSaudeService).buscarPorId(1L);
    }

    @Test
    void deveCriar() throws Exception {

        UnidadeSaudeDTO request = new UnidadeSaudeDTO();
        request.setNome("UBS Centro");

        UnidadeSaudeResponseDTO response = new UnidadeSaudeResponseDTO();
        response.setId(1L);
        response.setNome("UBS Centro");

        when(unidadeSaudeService.criar(any(UnidadeSaudeDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/unidades-saude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "nome":"UBS Centro",
                          "cnes":"1234567",
                          "tipoServico":"UBS",
                          "telefone":"51999999999",
                          "endereco":{
                            "rua":"Rua A",
                            "numero":"123",
                            "bairro":"Centro",
                            "cidade":"Porto Alegre",
                            "estado":"RS"
                          }
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("UBS Centro"));

        verify(unidadeSaudeService).criar(any(UnidadeSaudeDTO.class));
    }

    @Test
    void deveDeletar() throws Exception {

        doNothing().when(unidadeSaudeService).deletar(1L);

        mockMvc.perform(delete("/unidades-saude/1"))
                .andExpect(status().isNoContent());

        verify(unidadeSaudeService).deletar(1L);
    }

    /*@Test
    void deveListarTodos() throws Exception {

        when(unidadeSaudeService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/unidades-saude/all"))
                .andExpect(status().isOk());
    }*/

    @Test
    void deveAtualizarUnidadeSaude() throws Exception {

        EnderecoDTO endereco = new EnderecoDTO();
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("Porto Alegre");
        endereco.setEstado("RS");

        UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
        dto.setNome("UBS Central");
        dto.setCnes("1234567");
        dto.setEndereco(endereco);
        dto.setTipoServico(TipoServico.UBS); // ajuste para o enum real

        UnidadeSaudeResponseDTO response = new UnidadeSaudeResponseDTO();

        when(unidadeSaudeService.atualizar(eq(1L), any(UnidadeSaudeDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/unidades-saude/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}