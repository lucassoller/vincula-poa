package com.vincula.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vincula.entity.Endereco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GeocodingService geocodingService;

    @Test
    void devePreencherCoordenadas() throws Exception {

        Endereco endereco = new Endereco();
        endereco.setRua("Rua A");
        endereco.setNumero("123");
        endereco.setCidade("Porto Alegre");
        endereco.setEstado("RS");

        ResponseEntity<String> response =
                ResponseEntity.ok("[{\"lat\":\"-30.1\",\"lon\":\"-51.2\"}]");

        ArrayNode root = new ObjectMapper().createArrayNode();

        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("lat", "-30.1");
        node.put("lon", "-51.2");

        root.add(node);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);

        when(objectMapper.readTree(anyString()))
                .thenReturn(root);

        geocodingService.preencherCoordenadas(endereco);

        assertEquals(-30.1, endereco.getLatitude());
        assertEquals(-51.2, endereco.getLongitude());
    }

    @Test
    void naoDevePreencherQuandoArrayVazio() throws Exception {

        Endereco endereco = new Endereco();

        ResponseEntity<String> response =
                ResponseEntity.ok("[]");

        ArrayNode root = new ObjectMapper().createArrayNode();

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);

        when(objectMapper.readTree(anyString()))
                .thenReturn(root);

        geocodingService.preencherCoordenadas(endereco);

        assertNull(endereco.getLatitude());
        assertNull(endereco.getLongitude());
    }

    @Test
    void naoDevePreencherQuandoRespostaNaoForArray() throws Exception {

        Endereco endereco = new Endereco();

        ObjectNode root = new ObjectMapper().createObjectNode();

        ResponseEntity<String> response =
                ResponseEntity.ok("{}");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(response);

        when(objectMapper.readTree(anyString()))
                .thenReturn(root);

        geocodingService.preencherCoordenadas(endereco);

        assertNull(endereco.getLatitude());
        assertNull(endereco.getLongitude());
    }

    @Test
    void naoDeveLancarExcecaoQuandoApiFalhar() throws Exception {

        Endereco endereco = new Endereco();

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException());

        assertDoesNotThrow(() ->
                geocodingService.preencherCoordenadas(endereco));
    }
}