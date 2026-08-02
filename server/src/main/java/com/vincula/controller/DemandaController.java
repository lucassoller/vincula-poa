package com.vincula.controller;

import com.vincula.dto.demanda.*;
import com.vincula.dto.MotivoBuscaResponseDTO;
import com.vincula.dto.usuario.UsuarioFiltroResponseDTO;
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/filtradas")
    public ResponseEntity<Page<DemandaResponseDTO>> listarTodasFiltradas2(
            @RequestBody FiltroDemandaRequestDTO filtro,
            Pageable pageable) {
        return ResponseEntity.ok(demandaService.listarTodasFiltradas(filtro, pageable));
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/motivos")
    public ResponseEntity<List<MotivoBuscaResponseDTO>> listarMotivos() {
        return ResponseEntity.ok(demandaService.listarMotivos());
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @GetMapping("/usuarios/com-demandas")
    public ResponseEntity<List<UsuarioFiltroResponseDTO>> listarUsuariosComDemanda() {
        return ResponseEntity.ok(demandaService.listarUsuariosComDemanda());
    }

    @PreAuthorize("hasRole('SERVIDOR_APS')")
    @GetMapping("/usuarios/com-demandas/unidade/{unidadeId}")
    public ResponseEntity<List<UsuarioFiltroResponseDTO>> listarUsuariosComDemandaPorUnidade(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(demandaService.listarUsuariosComDemandaPorUnidade(unidadeId));
    }

    @PreAuthorize("hasRole('SOLICITANTE')")
    @GetMapping("/usuarios/com-demandas/solicitante/{unidadeId}")
    public ResponseEntity<List<UsuarioFiltroResponseDTO>> listarUsuariosComDemandaPorUnidadeSolicitante(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(demandaService.listarUsuariosComDemandaPorUnidadeSolicitante(unidadeId));
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/exportar", produces = "text/csv")
    public ResponseEntity<String> exportarDemandasCsv(@RequestBody FiltroDemandaRequestDTO filtro) {

        String csv = demandaService.exportarDemandasCsv(filtro);

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