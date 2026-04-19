package com.vincula.service;

import com.vincula.dto.PacienteDTO;
import com.vincula.dto.UnidadeSaudeRefDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.Paciente;
import com.vincula.entity.UnidadeSaude;
import com.vincula.repository.PacienteRepository;
import com.vincula.repository.UnidadeSaudeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final EnderecoService enderecoService;
    private final UnidadeSaudeService unidadeSaudeService;
    private final UnidadeSaudeRepository unidadeSaudeRepository;

    public PacienteService(PacienteRepository pacienteRepository, EnderecoService enderecoService,
                           UnidadeSaudeService unidadeSaudeService, UnidadeSaudeRepository unidadeSaudeRepository) {
        this.pacienteRepository = pacienteRepository;
        this.enderecoService = enderecoService;
        this.unidadeSaudeService = unidadeSaudeService;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
    }

    public PacienteDTO criar(PacienteDTO dto) {
        Paciente entity = toEntity(dto);

        Paciente salvo = pacienteRepository.save(entity);
        return toDTO(salvo);
    }

    public List<PacienteDTO> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PacienteDTO buscarPorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        return toDTO(paciente);
    }

    public PacienteDTO buscarPorCpf(String cpf) {
        Paciente paciente = pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o CPF informado"));

        return toDTO(paciente);
    }

    public PacienteDTO buscarPorCns(String cns) {
        Paciente paciente = pacienteRepository.findByCns(cns)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o CNS informado"));

        return toDTO(paciente);
    }

    public PacienteDTO atualizar(Long id, PacienteDTO dto) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        validarCpfECnsEIdDif(id, dto);

        UnidadeSaude unidadeSaude = unidadeSaudeRepository.findById(dto.getUnidadeSaude().getId())
                .orElseThrow(() -> new RuntimeException("Unidade de saúde não encontrada"));

        paciente.setUnidadeSaude(unidadeSaude);
        paciente.setNome(dto.getNome());
        paciente.setSobrenome(dto.getSobrenome());
        paciente.setTelefone(dto.getTelefone());
        paciente.setCpf(dto.getCpf());
        paciente.setCns(dto.getCns());

        Endereco endereco = paciente.getEndereco();

        endereco.setRua(dto.getEndereco().getRua());
        endereco.setNumero(dto.getEndereco().getNumero());
        endereco.setBairro(dto.getEndereco().getBairro());
        endereco.setCidade(dto.getEndereco().getCidade());
        endereco.setEstado(dto.getEndereco().getEstado());
        endereco.setCep(dto.getEndereco().getCep());
        endereco.setLatitude(dto.getEndereco().getLatitude());
        endereco.setLongitude(dto.getEndereco().getLongitude());

        Paciente atualizado = pacienteRepository.save(paciente);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        pacienteRepository.delete(paciente);
    }

    private void validarCpfECns(PacienteDTO dto) {
        if (pacienteRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (pacienteRepository.existsByCns(dto.getCns())) {
            throw new RuntimeException("CNS já cadastrado");
        }
    }

    private void validarCpfECnsEIdDif(Long id, PacienteDTO dto) {
        if (pacienteRepository.existsByCpfAndIdNot(dto.getCpf(), id)) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (pacienteRepository.existsByCnsAndIdNot(dto.getCns(), id)) {
            throw new RuntimeException("CNS já cadastrado");
        }
    }

    public Paciente toEntity(PacienteDTO dto) {
        validarCpfECns(dto);

        Endereco endereco = enderecoService.toEntity(dto.getEndereco());
        UnidadeSaude unidadeSaude = unidadeSaudeRepository.findById(dto.getUnidadeSaude().getId())
                .orElseThrow(() -> new RuntimeException("Unidade de saúde não encontrada"));

        Paciente entity = new Paciente();
        entity.setNome(dto.getNome());
        entity.setSobrenome(dto.getSobrenome());
        entity.setTelefone(dto.getTelefone());
        entity.setCpf(dto.getCpf());
        entity.setCns(dto.getCns());
        entity.setEndereco(endereco);
        entity.setUnidadeSaude(unidadeSaude);

        return entity;
    }

    public PacienteDTO toDTO(Paciente entity) {

        PacienteDTO dto = new PacienteDTO();

        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setSobrenome(entity.getSobrenome());
        dto.setTelefone(entity.getTelefone());
        dto.setCpf(entity.getCpf());
        dto.setCns(entity.getCns());
        dto.setEndereco(enderecoService.toDTO(entity.getEndereco()));
        dto.setUnidadeSaude(unidadeSaudeService.toDTO(entity.getUnidadeSaude()));

        return dto;
    }
}