package com.vincula.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vincula.util.AuditoriaFacade;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    private final AuditoriaFacade auditoriaFacade;
    private final Environment env;
    private final HttpClient httpClient;

    public EmailService(AuditoriaFacade auditoriaFacade, Environment env, HttpClient httpClient) {
        this.auditoriaFacade = auditoriaFacade;
        this.env = env;
        this.httpClient = httpClient;
    }

    public void enviarEmail(String endereco, String link) throws IOException {
        InputStream inputStream = getClass()
                .getResourceAsStream("/templates/redefinir-senha.html");

        if (inputStream == null) {
            throw new RuntimeException("Template não encontrado");
        }

        String html = new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8);

        html = html.replace("{{LINK_REDEFINICAO}}", link);

        String apiKey = env.getProperty("mail.token");

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode json = mapper.createObjectNode();
        json.put("from", "Vincula POA <onboarding@resend.dev>");
        json.putArray("to").add(endereco);
        json.put("subject", "Redefinição de senha - Vincula POA");
        json.put("html", html);

        String body = mapper.writeValueAsString(json);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                auditoriaFacade.emailEnviado(endereco);
            } else {
                auditoriaFacade.emailFalhou(endereco);
                throw new RuntimeException("Falha ao enviar email: " + response.body());
            }

        } catch (Exception e) {
            auditoriaFacade.emailFalhou(endereco);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

//etku jyil mqla ctca
//cfsx tuec tjgg jooe
//anvb rvmi kuhm qxkp
//re_ebjLxpEu_BR18XkDCkX5c6wrdcfJWBPSo"