package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.servico.*;
import com.vincula.enums.TipoServico;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.ServicoService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServicoController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class ServicoControllerTest {

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
    private ServicoService servicoService;

    @Test
    void deveBuscarPorId() throws Exception {

        ServicoResponseDTO dto = new ServicoResponseDTO();
        dto.setId(1L);
        dto.setNome("UBS Centro");

        when(servicoService.buscarPorId(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/servicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("UBS Centro"));

        verify(servicoService).buscarPorId(1L);
    }

    @Test
    void deveCriar() throws Exception {

        ServicoDTO request = new ServicoDTO();
        request.setNome("UBS Centro");

        ServicoResponseDTO response = new ServicoResponseDTO();
        response.setId(1L);
        response.setNome("UBS Centro");

        when(servicoService.criar(any(ServicoDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/servicos")
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

        verify(servicoService).criar(any(ServicoDTO.class));
    }

    @Test
    void deveDeletar() throws Exception {

        doNothing().when(servicoService).deletar(1L);

        mockMvc.perform(delete("/servicos/1"))
                .andExpect(status().isNoContent());

        verify(servicoService).deletar(1L);
    }

    @Test
    void deveAtualizarServico() throws Exception {

        EnderecoDTO endereco = new EnderecoDTO();
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("Porto Alegre");
        endereco.setEstado("RS");

        ServicoDTO dto = new ServicoDTO();
        dto.setNome("UBS Central");
        dto.setCnes("1234567");
        dto.setEndereco(endereco);
        dto.setTipoServico(TipoServico.UBS); // ajuste para o enum real

        ServicoResponseDTO response = new ServicoResponseDTO();

        when(servicoService.atualizar(eq(1L), any(ServicoDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/servicos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarTodosFiltrados() throws Exception {

        FiltroServicoRequestDTO filtro = new FiltroServicoRequestDTO();

        ServicoResponseDTO servico = new ServicoResponseDTO();
        servico.setId(1L);
        servico.setNome("UBS Centro");

        Page<ServicoResponseDTO> page =
                new PageImpl<>(List.of(servico));

        when(servicoService.listarTodosFiltrados(
                any(FiltroServicoRequestDTO.class),
                any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(post("/servicos/filtrados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filtro)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("UBS Centro"));

        verify(servicoService).listarTodosFiltrados(
                any(FiltroServicoRequestDTO.class),
                any(Pageable.class)
        );
    }

    @Test
    void deveListarTodos() throws Exception {

        ServicoShortResponseDTO ubs =
                new ServicoShortResponseDTO();
        ubs.setId(1L);
        ubs.setNome("UBS Centro");
        ubs.setCnes("1234567");

        ServicoShortResponseDTO outro =
                new ServicoShortResponseDTO();
        outro.setId(2L);
        outro.setNome("Outro Serviço");
        outro.setCnes("7654321");

        ServicosResponseDTO response = new ServicosResponseDTO(
                List.of(ubs, outro),
                List.of(ubs),
                List.of(outro),
                List.of(outro),
                List.of()
        );

        when(servicoService.listarServicos())
                .thenReturn(response);

        mockMvc.perform(get("/servicos/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todos", hasSize(2)))
                .andExpect(jsonPath("$.ubs", hasSize(1)))
                .andExpect(jsonPath("$.servicos", hasSize(1)))
                .andExpect(jsonPath("$.outros", hasSize(1)))
                .andExpect(jsonPath("$.especializados", hasSize(0)));

        verify(servicoService).listarServicos();
    }

    @Test
    void deveListarTodasServicos() throws Exception {

        ServicoShortResponseDTO dto =
                new ServicoShortResponseDTO();

        dto.setId(1L);
        dto.setNome("UBS Centro");
        dto.setCnes("1234567");

        when(servicoService.listarTodasServicos())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/servicos/ubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("UBS Centro"))
                .andExpect(jsonPath("$[0].cnes").value("1234567"));

        verify(servicoService).listarTodasServicos();
    }

    @Test
    void deveListarTodosServicos() throws Exception {

        ServicoShortResponseDTO dto =
                new ServicoShortResponseDTO();

        dto.setId(1L);
        dto.setNome("Serviço Especializado");
        dto.setCnes("1234567");

        when(servicoService.listarTodosServicos())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/servicos/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Serviço Especializado"))
                .andExpect(jsonPath("$[0].cnes").value("1234567"));

        verify(servicoService).listarTodosServicos();
    }
}