package com.vincula.service;

import com.vincula.dto.EnderecoDTO;
import com.vincula.entity.Endereco;
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
        Endereco entity = toEntity(dto);
        Endereco salvo = enderecoRepository.save(entity);
        return toDTO(salvo);
    }

    public List<EnderecoDTO> listarTodos() {
        return enderecoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EnderecoDTO buscarPorId(Long id) {
        Endereco entity = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        return toDTO(entity);
    }

    public EnderecoDTO atualizar(Long id, EnderecoDTO dto) {
        Endereco entity = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        entity.setRua(dto.getRua());
        entity.setNumero(dto.getNumero());
        entity.setBairro(dto.getBairro());
        entity.setCidade(dto.getCidade());
        entity.setEstado(dto.getEstado());
        entity.setCep(dto.getCep());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());

        Endereco atualizado = enderecoRepository.save(entity);
        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Endereco entity = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        enderecoRepository.delete(entity);
    }

    public Endereco toEntity(EnderecoDTO dto) {
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