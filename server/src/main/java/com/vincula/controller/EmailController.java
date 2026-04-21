package com.vincula.controller;

import com.vincula.dto.EmailDTO;
import com.vincula.service.EmailService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('EXECUTOR_APS')")
@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/enviar")
    public String enviar(@RequestBody EmailDTO dto) {
        emailService.enviarEmail(dto.getPara(), dto.getAssunto(), dto.getMensagem());
        return "E-mail enviado com sucesso!";
    }
}