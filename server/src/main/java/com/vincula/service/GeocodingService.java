package com.vincula.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.entity.Endereco;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeocodingService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void preencherCoordenadas(Endereco endereco) {

        try {

            String enderecoFormatado = String.format("%s, %s, %s, %s",
                    endereco.getRua(),
                    endereco.getNumero(),
                    endereco.getCidade(),
                    endereco.getEstado()
            );

            String url = "https://nominatim.openstreetmap.org/search" +
                    "?format=json" +
                    "&limit=1" +
                    "&q=" + enderecoFormatado;

            HttpHeaders headers = new HttpHeaders();

            headers.set("User-Agent", "VinculaPOA");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());

            if (root.isArray() && !root.isEmpty()) {

                JsonNode primeiro = root.get(0);

                endereco.setLatitude(primeiro.get("lat").asDouble());
                endereco.setLongitude(primeiro.get("lon").asDouble());
            }

        } catch (Exception ignored) {
        }
    }
}