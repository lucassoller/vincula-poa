package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.MotivoBuscaResponseDTO;
import com.vincula.dto.MotivoComplementoResponseDTO;
import com.vincula.dto.demanda.*;
import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.MotivoComplemento;
import com.vincula.enums.PrazoDemanda;
import com.vincula.enums.Prioridade;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.DemandaService;
import com.vincula.util.AuditoriaFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DemandaController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureJsonTesters
class DemandaControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
    void deveBuscarPorId() throws Exception {

        when(demandaService.buscarPorId(1L))
                .thenReturn(new DemandaResponseDTO());

        mockMvc.perform(get("/demandas/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarPorUsuario() throws Exception {

        when(demandaService.listarPorUsuario(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/demandas/usuario/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveCriarDemanda() throws Exception {

        DemandaDTO dto = new DemandaDTO();
        dto.setUsuarioId(1L);
        dto.setMotivoBuscaAtiva(MotivoBuscaAtiva.BOLSA_FAMILIA);
        dto.setMotivoComplemento(MotivoComplemento.ABANDONO_TRATAMENTO);
        dto.setDescricaoBusca("descricao");
        dto.setPrazoDemanda(PrazoDemanda.D7);
        dto.setPrioridade(Prioridade.BAIXA);

        when(demandaService.criar(any(DemandaDTO.class)))
                .thenReturn(new DemandaResponseDTO());

        mockMvc.perform(post("/demandas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveAtualizarDemanda() throws Exception {

        DemandaDTO dto = new DemandaDTO();
        dto.setUsuarioId(1L);
        dto.setMotivoBuscaAtiva(MotivoBuscaAtiva.BOLSA_FAMILIA);
        dto.setMotivoComplemento(MotivoComplemento.ABANDONO_TRATAMENTO);
        dto.setDescricaoBusca("descricao");
        dto.setPrazoDemanda(PrazoDemanda.D7);
        dto.setPrioridade(Prioridade.BAIXA);

        when(demandaService.atualizar(eq(1L), any(DemandaDTO.class)))
                .thenReturn(new DemandaResponseDTO());

        mockMvc.perform(put("/demandas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarMotivos() throws Exception {
        List<MotivoBuscaResponseDTO> motivos = List.of(
                new MotivoBuscaResponseDTO(
                        "PACIENTE_NAO_ENCONTRADO",
                        "Paciente não encontrado",
                        List.of(
                                new MotivoComplementoResponseDTO(
                                        "ENDERECO_INCORRETO",
                                        "Endereço incorreto"
                                )
                        )
                ),
                new MotivoBuscaResponseDTO(
                        "RECUSA_ATENDIMENTO",
                        "Recusa atendimento",
                        List.of()
                )
        );

        when(demandaService.listarMotivos()).thenReturn(motivos);

        mockMvc.perform(get("/demandas/motivos"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].valor").value("PACIENTE_NAO_ENCONTRADO"))
                .andExpect(jsonPath("$[0].descricao").value("Paciente não encontrado"))
                .andExpect(jsonPath("$[0].complementos[0].valor").value("ENDERECO_INCORRETO"))
                .andExpect(jsonPath("$[0].complementos[0].descricao").value("Endereço incorreto"))

                .andExpect(jsonPath("$[1].valor").value("RECUSA_ATENDIMENTO"))
                .andExpect(jsonPath("$[1].descricao").value("Recusa atendimento"))
                .andExpect(jsonPath("$[1].complementos").isArray())
                .andExpect(jsonPath("$[1].complementos").isEmpty());

        verify(demandaService).listarMotivos();
    }

    @Test
    void deveRedirecionarDemanda() throws Exception {

        RedirecionarDemandaDTO dto = new RedirecionarDemandaDTO();
        dto.setNovoServicoResponsavelId(2L);
        dto.setMotivoRedirecionamento("motivo");

        when(demandaService.redirecionar(
                eq(1L), any(RedirecionarDemandaDTO.class)))
                .thenReturn(new DemandaResponseDTO());

        mockMvc.perform(patch("/demandas/1/redirecionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveEncerrarDemanda() throws Exception {

        EncerrarDemandaDTO dto = new EncerrarDemandaDTO();
        dto.setDesfechoDemanda(DesfechoDemanda.OBITO);
        dto.setDescricaoDesfecho("descricao");

        when(demandaService.encerrar(
                eq(1L), any(EncerrarDemandaDTO.class)))
                .thenReturn(new DemandaResponseDTO());

        mockMvc.perform(patch("/demandas/1/encerrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveDeletarDemanda() throws Exception {

        doNothing().when(demandaService).deletar(1L);

        mockMvc.perform(delete("/demandas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveListarTodasFiltradas() throws Exception {

        FiltroDemandaRequestDTO filtro = new FiltroDemandaRequestDTO();

        Page<DemandaResponseDTO> page =
                new PageImpl<>(List.of(new DemandaResponseDTO()));

        when(demandaService.listarTodasFiltradas(
                any(FiltroDemandaRequestDTO.class),
                any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(post("/demandas/filtradas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filtro)))
                .andExpect(status().isOk());

        verify(demandaService).listarTodasFiltradas(
                any(FiltroDemandaRequestDTO.class),
                any(Pageable.class)
        );
    }

    @Test
    void deveExportarDemandasCsv() throws Exception {

        FiltroDemandaRequestDTO filtro = new FiltroDemandaRequestDTO();

        String csv = "Nome,Status\nLucas,ABERTA";

        when(demandaService.exportarDemandasCsv(
                any(FiltroDemandaRequestDTO.class)
        )).thenReturn(csv);

        mockMvc.perform(post("/demandas/exportar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filtro)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        matchesPattern(
                                "attachment; filename=demandas-vincula-poa-\\d{2}-\\d{2}-\\d{4}_\\d{2}-\\d{2}\\.csv"
                        )
                ))
                .andExpect(content().string(csv));

        verify(demandaService).exportarDemandasCsv(
                any(FiltroDemandaRequestDTO.class)
        );
    }
}