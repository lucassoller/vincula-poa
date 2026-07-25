package com.vincula.controller;

import com.vincula.dto.demanda.DemandaDTO;
import com.vincula.dto.demanda.EncerrarDemandaDTO;
import com.vincula.dto.demanda.RedirecionarDemandaDTO;
import com.vincula.dto.demanda.DemandaResponseDTO;
import com.vincula.dto.MotivoBuscaResponseDTO;
import com.vincula.enums.StatusDemanda;
import com.vincula.service.DemandaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/demandas")
public class DemandaController {

    private final DemandaService demandaService;

    public DemandaController(DemandaService demandaService) {
        this.demandaService = demandaService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<DemandaResponseDTO> criar(@Valid @RequestBody DemandaDTO dto) {
        return ResponseEntity.ok(demandaService.criar(dto));
    }

    @PreAuthorize("hasRole('SERVIDOR_APS')")
    @PutMapping("/{id}")
    public ResponseEntity<DemandaResponseDTO> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody DemandaDTO dto) {
        return ResponseEntity.ok(demandaService.atualizar(id, dto));
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @GetMapping
    public ResponseEntity<Page<DemandaResponseDTO>> listarTodas(Pageable pageable) {
        return ResponseEntity.ok(demandaService.listarTodas(pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<DemandaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(demandaService.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('SERVIDOR_APS','GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<DemandaResponseDTO>> listarTodasPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(demandaService.listarPorUsuario(usuarioId, pageable));
    }

    @PreAuthorize("hasAnyRole('SERVIDOR_APS','GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @GetMapping("/unidade/{unidadeSaudeId}")
    public ResponseEntity<Page<DemandaResponseDTO>> listarTodasPorUnidadeSaude(
            @PathVariable Long unidadeSaudeId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(demandaService.listarPorUnidadeSaude(unidadeSaudeId, pageable));
    }

    @PreAuthorize("hasAnyRole('SOLICITANTE','GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @GetMapping("/solicitante/{unidadeSolicitanteId}")
    public ResponseEntity<Page<DemandaResponseDTO>> listarTodasPorUnidadeSolicitante(
            @PathVariable Long unidadeSolicitanteId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(demandaService.listarPorUnidadeSolicitante(unidadeSolicitanteId, pageable));
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<DemandaResponseDTO>> listarTodasPorStatus(
            @PathVariable StatusDemanda status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(demandaService.listarPorStatus(status, pageable));
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @GetMapping("/filtradas/{filtro}")
    public ResponseEntity<Page<DemandaResponseDTO>> listarTodasFiltradas(@PathVariable String filtro, Pageable pageable) {
        return ResponseEntity.ok(demandaService.listarTodasFiltradas(filtro, pageable));
    }

    @PreAuthorize("hasRole('SERVIDOR_APS')")
    @GetMapping("/filtradas/unidade/{unidadeSaudeId}/{filtro}")
    public ResponseEntity<Page<DemandaResponseDTO>> listarTodasPorUnidadeSaudeFiltradas(
            @PathVariable Long unidadeSaudeId,
            @PathVariable String filtro,
            Pageable pageable
    ) {
        return ResponseEntity.ok(demandaService.listarPorUnidadeSaudeFiltradas(unidadeSaudeId, filtro, pageable));
    }

    @PreAuthorize("hasRole('SOLICITANTE')")
    @GetMapping("/filtradas/solicitante/{unidadeSolicitanteId}/{filtro}")
    public ResponseEntity<Page<DemandaResponseDTO>> listarTodasPorUnidadeSolicitanteFiltradas(
            @PathVariable Long unidadeSolicitanteId,
            @PathVariable String filtro,
            Pageable pageable
    ) {
        return ResponseEntity.ok(demandaService.listarPorUnidadeSolicitanteFiltradas(unidadeSolicitanteId, filtro, pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/motivos")
    public ResponseEntity<List<MotivoBuscaResponseDTO>> listarMotivos() {
        return ResponseEntity.ok(demandaService.listarMotivos());
    }

    @PreAuthorize("hasAnyRole('SERVIDOR_APS','GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @PatchMapping("/{id}/redirecionar")
    public ResponseEntity<DemandaResponseDTO> redirecionar(@PathVariable Long id,
                                                   @Valid @RequestBody RedirecionarDemandaDTO dto) {
        return ResponseEntity.ok(demandaService.redirecionar(id, dto));
    }

    @PreAuthorize("hasAnyRole('SERVIDOR_APS', 'GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<DemandaResponseDTO> encerrar(@PathVariable Long id,
                                                       @Valid @RequestBody EncerrarDemandaDTO dto) {

        return ResponseEntity.ok(demandaService.encerrar(id, dto));
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        demandaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @GetMapping(value = "/exportar", produces = "text/csv")
    public ResponseEntity<String> exportarDemandasCsv() {

        String csv = demandaService.exportarDemandasCsv();

        return gerarRespostaCsv(csv);
    }

    @PreAuthorize("hasRole('SERVIDOR_APS')")
    @GetMapping(value = "/exportar/unidade/{unidadeId}", produces = "text/csv")
    public ResponseEntity<String> exportarDemandasPorUnidadeCsv(
            @PathVariable Long unidadeId
    ) {

        String csv = demandaService.exportarDemandasPorUnidadeCsv(unidadeId);

        return gerarRespostaCsv(csv);
    }

    @PreAuthorize("hasRole('SOLICITANTE')")
    @GetMapping(value = "/exportar/solicitante/{unidadeId}", produces = "text/csv")
    public ResponseEntity<String> exportarDemandasPorUnidadeSolicitanteCsv(
            @PathVariable Long unidadeId
    ) {

        String csv = demandaService.exportarDemandasPorUnidadeSolicitanteCsv(unidadeId);

        return gerarRespostaCsv(csv);
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @GetMapping(value = "/exportar/filtradas/{filtro}", produces = "text/csv")
    public ResponseEntity<String> exportarDemandasFiltradasCsv(
            @PathVariable String filtro
    ) {

        String csv = demandaService.exportarDemandasFiltradasCsv(filtro);

        return gerarRespostaCsv(csv);
    }

    @PreAuthorize("hasRole('SERVIDOR_APS')")
    @GetMapping(
            value = "/exportar/filtradas/unidade/{unidadeId}/{filtro}",
            produces = "text/csv"
    )
    public ResponseEntity<String> exportarDemandasFiltradasPorUnidadeCsv(
            @PathVariable Long unidadeId,
            @PathVariable String filtro
    ) {

        String csv = demandaService
                .exportarDemandasFiltradasPorUnidadeCsv(unidadeId, filtro);

        return gerarRespostaCsv(csv);
    }

    @PreAuthorize("hasRole('SOLICITANTE')")
    @GetMapping(
            value = "/exportar/filtradas/solicitante/{unidadeId}/{filtro}",
            produces = "text/csv"
    )
    public ResponseEntity<String> exportarDemandasFiltradasPorUnidadeSolicitanteCsv(
            @PathVariable Long unidadeId,
            @PathVariable String filtro
    ) {

        String csv = demandaService
                .exportarDemandasFiltradasPorUnidadeSolicitanteCsv(unidadeId, filtro);

        return gerarRespostaCsv(csv);
    }

    private ResponseEntity<String> gerarRespostaCsv(String csv) {

        String dataHora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=demandas-vincula-poa-"+dataHora+".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}