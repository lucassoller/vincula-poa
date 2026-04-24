package com.vincula.service;

import com.vincula.dto.EnderecoDTO;
import com.vincula.entity.Endereco;
import com.vincula.enums.TipoAcaoAuditoria;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.EnderecoRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final AuditoriaService auditoriaService;

    public EnderecoService(EnderecoRepository enderecoRepository, AuditoriaService auditoriaService) {
        this.enderecoRepository = enderecoRepository;
        this.auditoriaService = auditoriaService;
    }

    public EnderecoDTO criar(EnderecoDTO dto) {
        Endereco entity = toEntity(dto);
        Endereco salvo = enderecoRepository.save(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.ENDERECO_CRIADO,
                "Endereco",
                salvo.getId(),
                "Endereço criado"
        );

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
        String descricaoLog = AuditoriaDescricaoUtil.enderecoAtualizado(entity, dto);

        preencherEndereco(entity, dto);

        Endereco atualizado = enderecoRepository.save(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.ENDERECO_ATUALIZADO,
                "Endereco",
                atualizado.getId(),
                descricaoLog
        );

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Endereco entity = buscarEnderecoPorId(id);
        Long enderecoId = entity.getId();

        enderecoRepository.delete(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.ENDERECO_DELETADO,
                "Endereco",
                enderecoId,
                "Endereço deletado"
        );
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