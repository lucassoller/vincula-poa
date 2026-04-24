package com.vincula.service;

import com.vincula.entity.Demanda;
import com.vincula.entity.Paciente;
import com.vincula.enums.TipoAcaoAuditoria;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.PacienteRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final AuditoriaService auditoriaService;
    private final DemandaRepository demandaRepository;
    private final PacienteRepository pacienteRepository;

    public EmailService(JavaMailSender mailSender, AuditoriaService auditoriaService, DemandaRepository demandaRepository, PacienteRepository pacienteRepository) {
        this.mailSender = mailSender;
        this.auditoriaService = auditoriaService;
        this.demandaRepository = demandaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public void enviarEmailDemanda(Long demandaId, String assunto, String mensagem) {
        Demanda demanda = buscarDemandaPorId(demandaId);

        Paciente paciente = demanda.getPaciente();

        validarEmailPaciente(paciente);

        enviarEmail(paciente.getEmail(), assunto, mensagem);

        auditoriaService.registrar(
                TipoAcaoAuditoria.EMAIL_ENVIADO,
                "Demanda",
                demanda.getId(),
                "Email enviado para paciente ID " + paciente.getId()
                        + " referente à demanda ID " + demanda.getId()
        );
    }

    public void enviarEmailPaciente(Long pacienteId, String assunto, String mensagem) {

        Paciente paciente = buscarPacientePorId(pacienteId);

        validarEmailPaciente(paciente);

        enviarEmail(paciente.getEmail(), assunto, mensagem);

        auditoriaService.registrar(
                TipoAcaoAuditoria.EMAIL_ENVIADO,
                "Paciente",
                paciente.getId(),
                "Email enviado diretamente para paciente ID " + paciente.getId()
        );
    }

    public void enviarEmail(String para, String assunto, String mensagem) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(para);
        email.setSubject(assunto);
        email.setText(mensagem);

        mailSender.send(email);
    }

    private Paciente buscarPacientePorId(Long pacienteId){
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new NotFoundException("Paciente não encontrado"));
    }

    private Demanda buscarDemandaPorId(Long demandaId){
        return demandaRepository.findById(demandaId)
                .orElseThrow(() -> new NotFoundException("Demanda não encontrada"));
    }

    private void validarEmailPaciente(Paciente paciente) {
        if (paciente.getEmail() == null || paciente.getEmail().isBlank()) {
            throw new BusinessException("Paciente não possui email cadastrado");
        }
    }
}

//etku jyil mqla ctca