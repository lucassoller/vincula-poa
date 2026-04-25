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
import com.vincula.util.AuditoriaDescricaoUtil;
import com.vincula.util.AuditoriaFacade;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UnidadeSaudeService {

    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final EnderecoMapper enderecoMapper;
    private final AuditoriaFacade auditoriaFacade;

    public UnidadeSaudeService(UnidadeSaudeRepository unidadeSaudeRepository,
                               EnderecoMapper enderecoMapper,
                               AuditoriaFacade auditoriaFacade) {
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.enderecoMapper = enderecoMapper;
        this.auditoriaFacade = auditoriaFacade;
    }

    public UnidadeSaudeDTO criar(UnidadeSaudeDTO dto) {
        validarCnesCreate(dto);

        UnidadeSaude entity = toEntity(dto);

        UnidadeSaude salvo = unidadeSaudeRepository.save(entity);

        auditoriaFacade.unidadeSaudeCriada(salvo.getId());

        return toDTO(salvo);
    }

    public List<UnidadeSaudeDTO> listarTodos() {
        auditoriaFacade.unidadeSaudeVisualizada(0L);
        return unidadeSaudeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<PacienteDTO> listarPacientesPorUnidade(Long unidadeSaudeId) {
        auditoriaFacade.unidadeSaudeVisualizada(0L);

        return unidadeSaudeRepository.findPacientesByUnidadeSaudeId(unidadeSaudeId)
                .stream()
                .map(this::toPacienteDTO)
                .toList();
    }

    public UnidadeSaudeDTO buscarPorId(Long id) {
        UnidadeSaude entity = buscarUnidadeSaudePorId(id);

        auditoriaFacade.unidadeSaudeVisualizada(entity.getId());

        return toDTO(entity);
    }

    public UnidadeSaudeDTO buscarPorCnes(String cnes) {
        UnidadeSaude entity = buscarUnidadeSaudePorCnes(cnes);

        auditoriaFacade.unidadeSaudeVisualizada(entity.getId());

        return toDTO(entity);
    }

    public UnidadeSaudeDTO atualizar(Long id, UnidadeSaudeDTO dto) {

        UnidadeSaude entity = buscarUnidadeSaudePorId(id);

        validarCnesUpdate(dto, id);

        String descricaoLog = AuditoriaDescricaoUtil.unidadeSaudeAtualizada(entity, dto);

        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());
        entity.setTelefone(dto.getTelefone());

        enderecoMapper.updateEntityFromDto(dto.getEndereco(), entity.getEndereco());

        UnidadeSaude atualizado = unidadeSaudeRepository.save(entity);

        auditoriaFacade.unidadeSaudeAtualizada(atualizado.getId(), descricaoLog);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        UnidadeSaude entity = buscarUnidadeSaudePorId(id);

        Long unidadeId = entity.getId();

        unidadeSaudeRepository.delete(entity);

        auditoriaFacade.unidadeSaudeDeletada(unidadeId);
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

    private UnidadeSaude buscarUnidadeSaudePorCnes(String cnes){
        return unidadeSaudeRepository.findByCnes(cnes)
                .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));
    }

    public UnidadeSaude toEntity(UnidadeSaudeDTO dto){
        Endereco endereco = enderecoMapper.toEntity(dto.getEndereco());

        UnidadeSaude entity = new UnidadeSaude();
        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());
        entity.setTelefone(dto.getTelefone());
        entity.setEndereco(endereco);

        return entity;
    }

    public UnidadeSaudeDTO toDTO(UnidadeSaude entity) {
        UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCnes(entity.getCnes());
        dto.setTelefone(entity.getTelefone());
        dto.setEndereco(enderecoMapper.toDTO(entity.getEndereco()));
        return dto;
    }

    private PacienteDTO toPacienteDTO(Paciente entity) {
        PacienteDTO dto = new PacienteDTO();

        dto.setId(entity.getId());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setTelefone(entity.getTelefone());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setCpf(entity.getCpf());
        dto.setCns(entity.getCns());
        dto.setEndereco(enderecoMapper.toDTO(entity.getEndereco()));

        return dto;
    }
}