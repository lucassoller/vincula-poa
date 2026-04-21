package com.vincula.service;

import com.vincula.dto.PacienteDTO;
import com.vincula.dto.UnidadeSaudeDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.UnidadeSaudeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UnidadeSaudeService {

    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final EnderecoMapper enderecoMapper;

    public UnidadeSaudeService(UnidadeSaudeRepository unidadeSaudeRepository, EnderecoMapper enderecoMapper) {
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.enderecoMapper = enderecoMapper;
    }

    public UnidadeSaudeDTO criar(UnidadeSaudeDTO dto) {
        validarCnesCreate(dto);

        UnidadeSaude entity = toEntity(dto);

        UnidadeSaude salvo = unidadeSaudeRepository.save(entity);
        return toDTO(salvo);
    }

    public List<UnidadeSaudeDTO> listarTodos() {
        return unidadeSaudeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<PacienteDTO> listarPacientesPorUnidade(Long unidadeSaudeId) {
        List<Paciente> pacientes = unidadeSaudeRepository.findPacientesByUnidadeSaudeId(unidadeSaudeId);

        return pacientes.stream()
                .map(this::toPacienteDTO)
                .toList();
    }

    public UnidadeSaudeDTO buscarPorId(Long id) {
        UnidadeSaude entity = buscarUnidadeSaudePorId(id);

        return toDTO(entity);
    }

    public UnidadeSaudeDTO buscarPorCnes(String cnes) {
        UnidadeSaude entity = unidadeSaudeRepository.findByCnes(cnes)
                .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));

        return toDTO(entity);
    }

    public UnidadeSaudeDTO atualizar(Long id, UnidadeSaudeDTO dto) {

        UnidadeSaude entity = buscarUnidadeSaudePorId(id);

        validarCnesUpdate(dto, id);

        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());

        enderecoMapper.updateEntityFromDto(dto.getEndereco(), entity.getEndereco());

        UnidadeSaude atualizado = unidadeSaudeRepository.save(entity);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        UnidadeSaude entity = buscarUnidadeSaudePorId(id);

        unidadeSaudeRepository.delete(entity);
    }

    private void validarCnesCreate(UnidadeSaudeDTO dto) {
        if (unidadeSaudeRepository.existsByCnes(dto.getCnes())) {
            throw new ConflictException("CNES já cadastrado");
        }
    }

    private void validarCnesUpdate(UnidadeSaudeDTO dto, Long id) {
        if (unidadeSaudeRepository.existsByCnesAndIdNot(dto.getCnes(), id)) {
            throw new ConflictException("CNES já cadastrado");
        }
    }

    private UnidadeSaude buscarUnidadeSaudePorId(Long id) {
        return unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));
    }

    public UnidadeSaude toEntity(UnidadeSaudeDTO dto){
        Endereco endereco = enderecoMapper.toEntity(dto.getEndereco());

        UnidadeSaude entity = new UnidadeSaude();
        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());
        entity.setEndereco(endereco);

        return entity;
    }

    public UnidadeSaudeDTO toDTO(UnidadeSaude entity) {
        UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCnes(entity.getCnes());
        dto.setEndereco(enderecoMapper.toDTO(entity.getEndereco()));
        return dto;
    }

    private PacienteDTO toPacienteDTO(Paciente entity) {
        PacienteDTO dto = new PacienteDTO();

        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setSobrenome(entity.getSobrenome());
        dto.setTelefone(entity.getTelefone());
        dto.setCpf(entity.getCpf());
        dto.setCns(entity.getCns());
        dto.setEndereco(enderecoMapper.toDTO(entity.getEndereco()));

        return dto;
    }
}