package com.vincula.service;

import com.vincula.dto.EnderecoDTO;
import com.vincula.dto.PacienteDTO;
import com.vincula.entity.EnderecoEntity;
import com.vincula.repository.EnderecoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public EnderecoDTO criar(EnderecoDTO dto) {
        EnderecoEntity entity = toEntity(dto);
        EnderecoEntity salvo = enderecoRepository.save(entity);
        return toDTO(salvo);
    }

    public List<EnderecoDTO> listarTodos() {
        return enderecoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EnderecoDTO buscarPorId(Long id) {
        EnderecoEntity entity = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        return toDTO(entity);
    }

    public EnderecoDTO atualizar(Long id, EnderecoDTO dto) {
        EnderecoEntity entity = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        entity.setRua(dto.getRua());
        entity.setNumero(dto.getNumero());
        entity.setBairro(dto.getBairro());
        entity.setCidade(dto.getCidade());
        entity.setEstado(dto.getEstado());
        entity.setCep(dto.getCep());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());

        EnderecoEntity atualizado = enderecoRepository.save(entity);
        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        EnderecoEntity entity = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        enderecoRepository.delete(entity);
    }

    public EnderecoEntity toEntity(EnderecoDTO dto) {
        EnderecoEntity entity = new EnderecoEntity();

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

    public EnderecoDTO toDTO(EnderecoEntity entity) {
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
}