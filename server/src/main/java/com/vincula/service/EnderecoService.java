package com.vincula.service;

import com.vincula.dto.EnderecoDTO;
import com.vincula.entity.Endereco;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.EnderecoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .toList();
    }

    public EnderecoDTO buscarPorId(Long id) {
        return toDTO(buscarEnderecoPorId(id));
    }

    public EnderecoDTO atualizar(Long id, EnderecoDTO dto) {
        Endereco entity = buscarEnderecoPorId(id);
        preencherEndereco(entity, dto);

        Endereco atualizado = enderecoRepository.save(entity);
        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Endereco entity = buscarEnderecoPorId(id);
        enderecoRepository.delete(entity);
    }

    private Endereco buscarEnderecoPorId(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Endereço não encontrado"));
    }

    private Endereco toEntity(EnderecoDTO dto) {
        Endereco entity = new Endereco();
        preencherEndereco(entity, dto);
        return entity;
    }

    private EnderecoDTO toDTO(Endereco entity) {
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

    private void preencherEndereco(Endereco entity, EnderecoDTO dto) {
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