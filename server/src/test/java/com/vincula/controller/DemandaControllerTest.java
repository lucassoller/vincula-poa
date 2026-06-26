package com.vincula.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.demanda.DemandaDTO;
import com.vincula.dto.demanda.DemandaResponseDTO;
import com.vincula.dto.demanda.EncerrarDemandaDTO;
import com.vincula.dto.demanda.RedirecionarDemandaDTO;
import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.PrazoDemanda;
import com.vincula.security.JwtAuthenticationFilter;
import com.vincula.security.JwtService;
import com.vincula.service.DemandaService;
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
    void deveListarTodas() throws Exception {

        when(demandaService.listarTodas(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/demandas"))
                .andExpect(status().isOk());
    }

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
    void deveListarPorUnidade() throws Exception {

        when(demandaService.listarPorUnidadeSaude(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/demandas/unidade/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarPorSolicitante() throws Exception {

        when(demandaService.listarPorUnidadeSolicitante(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/demandas/solicitante/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarPorStatus() throws Exception {

        when(demandaService.listarPorStatus(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/demandas/status/ABERTA"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarFiltradas() throws Exception {

        when(demandaService.listarTodasFiltradas(eq("teste"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/demandas/filtradas/teste"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarFiltradasPorUnidade() throws Exception {

        when(demandaService.listarPorUnidadeSaudeFiltradas(
                eq(1L), eq("teste"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/demandas/filtradas/unidade/1/teste"))
                .andExpect(status().isOk());
    }

    @Test
    void deveListarFiltradasPorSolicitante() throws Exception {

        when(demandaService.listarPorUnidadeSolicitanteFiltradas(
                eq(1L), eq("teste"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/demandas/filtradas/solicitante/1/teste"))
                .andExpect(status().isOk());
    }

    @Test
    void deveCriarDemanda() throws Exception {

        DemandaDTO dto = new DemandaDTO();
        dto.setUsuarioId(1L);
        dto.setMotivoBuscaAtiva(MotivoBuscaAtiva.OUTRO);
        dto.setDescricaoBusca("descricao");
        dto.setPrazoDemanda(PrazoDemanda.D7);

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
        dto.setMotivoBuscaAtiva(MotivoBuscaAtiva.OUTRO);
        dto.setDescricaoBusca("descricao");
        dto.setPrazoDemanda(PrazoDemanda.D7);

        when(demandaService.atualizar(eq(1L), any(DemandaDTO.class)))
                .thenReturn(new DemandaResponseDTO());

        mockMvc.perform(put("/demandas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deveRedirecionarDemanda() throws Exception {

        RedirecionarDemandaDTO dto = new RedirecionarDemandaDTO();
        dto.setNovaUnidadeResponsavelId(2L);
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
    void deveExportarCsv() throws Exception {

        when(demandaService.exportarDemandasCsv())
                .thenReturn("id,nome\n1,teste");

        mockMvc.perform(get("/demandas/exportar"))
                .andExpect(status().isOk());
    }

    @Test
    void deveExportarPorUnidadeCsv() throws Exception {

        when(demandaService.exportarDemandasPorUnidadeCsv(1L))
                .thenReturn("id,nome\n1,teste");

        mockMvc.perform(get("/demandas/exportar/unidade/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveExportarPorUnidadeSolicitanteCsv() throws Exception {

        when(demandaService.exportarDemandasPorUnidadeSolicitanteCsv(1L))
                .thenReturn("id,nome\n1,teste");

        mockMvc.perform(get("/demandas/exportar/solicitante/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deveExportarFiltradasCsv() throws Exception {

        when(demandaService.exportarDemandasFiltradasCsv("teste"))
                .thenReturn("id,nome\n1,teste");

        mockMvc.perform(get("/demandas/exportar/filtradas/teste"))
                .andExpect(status().isOk());
    }

    @Test
    void deveExportarFiltradasPorUnidadeCsv() throws Exception {

        when(demandaService.exportarDemandasFiltradasPorUnidadeCsv(1L, "teste"))
                .thenReturn("id,nome\n1,teste");

        mockMvc.perform(get("/demandas/exportar/filtradas/unidade/1/teste"))
                .andExpect(status().isOk());
    }

    @Test
    void deveExportarFiltradasPorUnidadeSolicitanteCsv() throws Exception {

        when(demandaService.exportarDemandasFiltradasPorUnidadeSolicitanteCsv(1L, "teste"))
                .thenReturn("id,nome\n1,teste");

        mockMvc.perform(get("/demandas/exportar/filtradas/solicitante/1/teste"))
                .andExpect(status().isOk());
    }
}