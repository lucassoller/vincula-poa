package com.vincula.service;

import com.vincula.dto.PacienteDTO;
import com.vincula.dto.UnidadeSaudeDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.enums.TipoAcaoAuditoria;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.mapper.EnderecoMapper;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.util.AuditoriaDescricaoUtil;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UnidadeSaudeService {

    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final EnderecoMapper enderecoMapper;
    private final AuditoriaService auditoriaService;

    public UnidadeSaudeService(UnidadeSaudeRepository unidadeSaudeRepository, EnderecoMapper enderecoMapper, AuditoriaService auditoriaService) {
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.enderecoMapper = enderecoMapper;
        this.auditoriaService = auditoriaService;
    }

    public UnidadeSaudeDTO criar(UnidadeSaudeDTO dto) {
        validarCnesCreate(dto);

        UnidadeSaude entity = toEntity(dto);

        UnidadeSaude salvo = unidadeSaudeRepository.save(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.UNIDADE_SAUDE_CRIADA,
                "UnidadeSaude",
                salvo.getId(),
                "Unidade de saúde criada: " + salvo.getNome()
        );

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

        String descricaoLog = AuditoriaDescricaoUtil.unidadeSaudeAtualizada(entity, dto);

        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());
        entity.setTelefone(dto.getTelefone());

        enderecoMapper.updateEntityFromDto(dto.getEndereco(), entity.getEndereco());

        UnidadeSaude atualizado = unidadeSaudeRepository.save(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.UNIDADE_SAUDE_ATUALIZADA,
                "UnidadeSaude",
                atualizado.getId(),
                descricaoLog
        );

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        UnidadeSaude entity = buscarUnidadeSaudePorId(id);

        Long unidadeId = entity.getId();

        unidadeSaudeRepository.delete(entity);

        auditoriaService.registrar(
                TipoAcaoAuditoria.UNIDADE_SAUDE_DELETADA,
                "UnidadeSaude",
                unidadeId,
                "Unidade de saúde deletada"
        );
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