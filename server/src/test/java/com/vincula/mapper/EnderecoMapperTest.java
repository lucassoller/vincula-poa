package com.vincula.mapper;

import com.vincula.dto.endereco.EnderecoDTO;
import com.vincula.dto.endereco.EnderecoResponseDTO;
import com.vincula.entity.Endereco;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnderecoMapperTest {

    private final EnderecoMapper mapper = new EnderecoMapper();

    @Test
    void deveConverterDtoParaEntity() {

        EnderecoDTO dto = new EnderecoDTO();
        dto.setRua("Rua A");
        dto.setNumero("10");
        dto.setBairro("Centro");
        dto.setCidade("POA");
        dto.setEstado("RS");
        dto.setComplemento("apto 1");

        Endereco entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("Rua A", entity.getRua());
        assertEquals("10", entity.getNumero());
        assertEquals("Centro", entity.getBairro());
        assertEquals("POA", entity.getCidade());
        assertEquals("RS", entity.getEstado());
        assertEquals("apto 1", entity.getComplemento());
    }

    @Test
    void deveRetornarNullNoToEntityQuandoDtoForNull() {

        Endereco entity = mapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void deveConverterEntityParaDto() {

        Endereco entity = new Endereco();
        entity.setId(1L);
        entity.setRua("Rua A");
        entity.setNumero("10");
        entity.setBairro("Centro");
        entity.setCidade("POA");
        entity.setEstado("RS");
        entity.setComplemento("apto 1");

        EnderecoResponseDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Rua A", dto.getRua());
        assertEquals("10", dto.getNumero());
        assertEquals("Centro", dto.getBairro());
        assertEquals("POA", dto.getCidade());
        assertEquals("RS", dto.getEstado());
        assertEquals("apto 1", dto.getComplemento());
    }

    @Test
    void deveRetornarNullNoToDtoQuandoEntityForNull() {

        EnderecoResponseDTO dto = mapper.toDTO(null);

        assertNull(dto);
    }

    @Test
    void deveAtualizarEntityComDto() {

        Endereco entity = new Endereco();
        entity.setRua("Antiga Rua");
        entity.setNumero("1");
        entity.setBairro("Velho");
        entity.setCidade("Cidade X");
        entity.setEstado("XX");
        entity.setComplemento("old");

        EnderecoDTO dto = new EnderecoDTO();
        dto.setRua("Nova Rua");
        dto.setNumero("999");
        dto.setBairro("Centro");
        dto.setCidade("POA");
        dto.setEstado("RS");
        dto.setComplemento("novo");

        mapper.updateEntityFromDto(dto, entity);

        assertEquals("Nova Rua", entity.getRua());
        assertEquals("999", entity.getNumero());
        assertEquals("Centro", entity.getBairro());
        assertEquals("POA", entity.getCidade());
        assertEquals("RS", entity.getEstado());
        assertEquals("novo", entity.getComplemento());
    }
}