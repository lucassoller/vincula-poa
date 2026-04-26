package com.vincula.controller;

import com.vincula.dto.EmailDTO;
import com.vincula.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyRole('SOLICITANTE','EXECUTOR_APS')")
@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/enviar")
    public String enviar(@RequestBody EmailDTO dto) {
        emailService.enviarEmail(dto);
        return "E-mail enviado com sucesso!";
    }

    @PostMapping("/enviar/demanda/{demandaId}")
    public ResponseEntity<Void> enviarEmailDemanda(
            @PathVariable Long demandaId,
            @RequestBody EmailDTO dto
    ) {
        emailService.enviarEmailDemanda(demandaId, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/enviar/paciente/{pacienteId}")
    public ResponseEntity<Void> enviarEmailPaciente(
            @PathVariable Long pacienteId,
            @RequestBody EmailDTO dto
    ) {
        emailService.enviarEmailPaciente(pacienteId, dto);
        return ResponseEntity.ok().build();
    }
}