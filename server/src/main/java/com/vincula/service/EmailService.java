package com.vincula.service;

import com.vincula.entity.Demanda;
import com.vincula.entity.Paciente;
import com.vincula.exception.BusinessException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.DemandaRepository;
import com.vincula.repository.PacienteRepository;
import com.vincula.util.AuditoriaFacade;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final DemandaRepository demandaRepository;
    private final PacienteRepository pacienteRepository;
    private final AuditoriaFacade auditoriaFacade;

    public EmailService(JavaMailSender mailSender,
                        DemandaRepository demandaRepository,
                        PacienteRepository pacienteRepository,
                        AuditoriaFacade auditoriaFacade) {
        this.mailSender = mailSender;
        this.demandaRepository = demandaRepository;
        this.pacienteRepository = pacienteRepository;
        this.auditoriaFacade = auditoriaFacade;
    }

    public void enviarEmailDemanda(Long demandaId, String assunto, String mensagem) {
        Demanda demanda = buscarDemandaPorId(demandaId);

        Paciente paciente = demanda.getPaciente();

        validarEmailPaciente(paciente);

        try {
            enviarEmail(paciente.getEmail(), assunto, mensagem);
        } catch (Exception e) {
            auditoriaFacade.emailFalhou("Demanda", demanda.getId(), "Falha ao enviar email para paciente ID " + paciente.getId());

            throw e;
        }

        auditoriaFacade.emailEnviadoPorDemanda(demanda.getId(), paciente.getId());
    }

    public void enviarEmailPaciente(Long pacienteId, String assunto, String mensagem) {

        Paciente paciente = buscarPacientePorId(pacienteId);

        validarEmailPaciente(paciente);

        try {
            enviarEmail(paciente.getEmail(), assunto, mensagem);
        } catch (Exception e) {
            auditoriaFacade.emailFalhou("Paciente", paciente.getId(), "Falha ao enviar email para paciente ID " + paciente.getId());
            throw e;
        }

        auditoriaFacade.emailEnviadoPorPaciente(paciente.getId());
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