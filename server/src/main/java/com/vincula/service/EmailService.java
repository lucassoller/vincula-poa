package com.vincula.service;

import com.vincula.exception.BusinessException;
import com.vincula.util.AuditoriaFacade;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final AuditoriaFacade auditoriaFacade;

    public EmailService(JavaMailSender mailSender,
                        AuditoriaFacade auditoriaFacade) {
        this.mailSender = mailSender;
        this.auditoriaFacade = auditoriaFacade;
    }

    public void enviarEmail(String endereco, String link) throws MessagingException, IOException {
        InputStream inputStream = getClass()
                .getResourceAsStream("/templates/redefinir-senha.html");

        assert inputStream != null;
        String html = new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
        );

        html = html.replace("{{LINK_REDEFINICAO}}", link);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(endereco);

        helper.setSubject("Redefinição de senha - Vincula POA");

        helper.setText(html, true);

        try{
            mailSender.send(message);
            auditoriaFacade.emailEnviado(endereco);
        } catch (MailException e) {
            auditoriaFacade.emailFalhou(endereco);
            throw new BusinessException(e.getMessage());
        }
    }
}

//etku jyil mqla ctca
//cfsx tuec tjgg jooe
//anvb rvmi kuhm qxkp