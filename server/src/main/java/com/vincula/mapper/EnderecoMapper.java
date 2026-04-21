package com.vincula.mapper;

import com.vincula.dto.EnderecoDTO;
import com.vincula.entity.Endereco;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {

    public Endereco toEntity(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }

        Endereco entity = new Endereco();
        entity.setRua(dto.getRua());
        entity.setNumero(dto.getNumero());
        entity.setBairro(dto.getBairro());
        entity.setCidade(dto.getCidade());
        entity.setEstado(dto.getEstado());
        entity.setCep(dto.getCep());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        return entity;
    }

    public EnderecoDTO toDTO(Endereco entity) {
        if (entity == null) {
            return null;
        }

        EnderecoDTO dto = new EnderecoDTO();
        dto.setId(entity.getId());
        dto.setRua(entity.getRua());
        dto.setNumero(entity.getNumero());
        dto.setBairro(entity.getBairro());
        dto.setCidade(entity.getCidade());
        dto.setEstado(entity.getEstado());
        dto.setCep(entity.getCep());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        return dto;
    }

    public void updateEntityFromDto(EnderecoDTO dto, Endereco entity) {
        entity.setRua(dto.getRua());
        entity.setNumero(dto.getNumero());
        entity.setBairro(dto.getBairro());
        entity.setCidade(dto.getCidade());
        entity.setEstado(dto.getEstado());
        entity.setCep(dto.getCep());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
    }
}