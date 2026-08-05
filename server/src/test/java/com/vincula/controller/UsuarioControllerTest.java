package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.demanda.RedirecionarDemandaDTO;
import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.usuario.UsuarioDTO;
import com.vincula.dto.usuario.UsuarioResponseDTO;
import com.vincula.enums.Sexo;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.DemandaService;
import com.vincula.service.UsuarioService;
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

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private DemandaService demandaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuditoriaFacade auditoriaFacade;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveCriarUsuario() throws Exception {

        EnderecoDTO dtoEnd = new EnderecoDTO();
        dtoEnd.setRua("Rua A");
        dtoEnd.setNumero("123");
        dtoEnd.setBairro("Centro");
        dtoEnd.setCidade("Porto Alegre");
        dtoEnd.setEstado("RS");

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNomeCompleto("nome");
        dto.setDocumento("12345678911");
        dto.setSexo(Sexo.MASCULINO);
        dto.setTelefone("5199678955");
        dto.setEndereco(dtoEnd);


        when(usuarioService.criar(any(dto.getClass())))
                .thenReturn(new UsuarioResponseDTO());

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveListarUsuarios() throws Exception {

        when(usuarioService.listarTodos(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarTodosShort() throws Exception {

        when(usuarioService.listarTodos())
                .thenReturn(List.of());

        mockMvc.perform(get("/usuarios/all"))
                .andExpect(status().isOk());
    }


    @Test
    void deveListarFiltradosPorNomeOuDocumento() throws Exception {

        when(usuarioService.listarTodosFiltradosPorNomeOuDocumento("teste"))
                .thenReturn(List.of());

        mockMvc.perform(get("/usuarios/filtrados/busca/teste"))
                .andExpect(status().isOk());
    }

    @Test
    void deveBuscarPorId() throws Exception {

        when(usuarioService.buscarPorId(1L))
                .thenReturn(new UsuarioResponseDTO());

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveAtualizarUsuario() throws Exception {

        EnderecoDTO dtoEnd = new EnderecoDTO();
        dtoEnd.setRua("Rua A");
        dtoEnd.setNumero("123");
        dtoEnd.setBairro("Centro");
        dtoEnd.setCidade("Porto Alegre");
        dtoEnd.setEstado("RS");

        UsuarioDTO dto = new UsuarioDTO();
        dto.setNomeCompleto("nome");
        dto.setDocumento("12345678911");
        dto.setSexo(Sexo.MASCULINO);
        dto.setTelefone("5199678955");
        dto.setEndereco(dtoEnd);

        when(usuarioService.atualizar(eq(1L), any(dto.getClass())))
                .thenReturn(new UsuarioResponseDTO());

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveRedirecionarDemandas() throws Exception {

        RedirecionarDemandaDTO dto = new RedirecionarDemandaDTO();
        dto.setMotivoRedirecionamento("motivo");
        dto.setNovaUnidadeResponsavelId(1L);

        doNothing().when(demandaService)
                .redirecionarDemandasAbertasDoUsuario(eq(1L), any(dto.getClass()));

        mockMvc.perform(patch("/usuarios/1/redirecionar-abertas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveDeletarUsuario() throws Exception {

        doNothing().when(usuarioService).deletar(1L);

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());
    }
}