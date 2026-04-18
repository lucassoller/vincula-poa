package com.vincula.service;

import com.vincula.dto.EnderecoDTO;
import com.vincula.dto.PacienteDTO;
import com.vincula.entity.EnderecoEntity;
import com.vincula.entity.PacienteEntity;
import com.vincula.repository.EnderecoRepository;
import com.vincula.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final EnderecoService enderecoService;

    public PacienteService(PacienteRepository pacienteRepository, EnderecoRepository enderecoRepository, EnderecoService enderecoService) {
        this.pacienteRepository = pacienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.enderecoService = enderecoService;
    }

    public PacienteDTO criar(PacienteDTO dto) {
        PacienteEntity entity = toEntity(dto);

        PacienteEntity salvo = pacienteRepository.save(entity);
        return toDTO(salvo);
    }

    public List<PacienteDTO> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PacienteDTO buscarPorId(Long id) {
        PacienteEntity paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        return toDTO(paciente);
    }

    public PacienteDTO buscarPorCpf(String cpf) {
        PacienteEntity paciente = pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o CPF informado"));

        return toDTO(paciente);
    }

    public PacienteDTO buscarPorCns(String cns) {
        PacienteEntity paciente = pacienteRepository.findByCns(cns)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o CNS informado"));

        return toDTO(paciente);
    }

    public PacienteDTO atualizar(Long id, PacienteDTO dto) {
        PacienteEntity paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        validarCpfECnsEIdDif(id, dto);

        paciente.setNome(dto.getNome());
        paciente.setSobrenome(dto.getSobrenome());
        paciente.setTelefone(dto.getTelefone());
        paciente.setCpf(dto.getCpf());
        paciente.setCns(dto.getCns());

        EnderecoEntity endereco = paciente.getEndereco();

        endereco.setRua(dto.getEndereco().getRua());
        endereco.setNumero(dto.getEndereco().getNumero());
        endereco.setBairro(dto.getEndereco().getBairro());
        endereco.setCidade(dto.getEndereco().getCidade());
        endereco.setEstado(dto.getEndereco().getEstado());
        endereco.setCep(dto.getEndereco().getCep());
        endereco.setLatitude(dto.getEndereco().getLatitude());
        endereco.setLongitude(dto.getEndereco().getLongitude());

        PacienteEntity atualizado = pacienteRepository.save(paciente);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        PacienteEntity paciente = pacienteRepository.findById(id)
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

        if (pacienteRepository.existsByCpfAndIdNot(dto.getCns(), id)) {
            throw new RuntimeException("CNS já cadastrado");
        }
    }

    private PacienteEntity toEntity(PacienteDTO dto) {
        validarCpfECns(dto);

        EnderecoEntity endereco = enderecoService.toEntity(dto.getEndereco());

        PacienteEntity entity = new PacienteEntity();
        entity.setNome(dto.getNome());
        entity.setSobrenome(dto.getSobrenome());
        entity.setTelefone(dto.getTelefone());
        entity.setCpf(dto.getCpf());
        entity.setCns(dto.getCns());
        entity.setEndereco(endereco);

        return entity;
    }

    private PacienteDTO toDTO(PacienteEntity entity) {

        PacienteDTO dto = new PacienteDTO();

        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setSobrenome(entity.getSobrenome());
        dto.setTelefone(entity.getTelefone());
        dto.setCpf(entity.getCpf());
        dto.setCns(entity.getCns());

        dto.setEndereco(new com.vincula.dto.EnderecoDTO());
        dto.getEndereco().setId(entity.getEndereco().getId());
        dto.getEndereco().setRua(entity.getEndereco().getRua());
        dto.getEndereco().setNumero(entity.getEndereco().getNumero());
        dto.getEndereco().setBairro(entity.getEndereco().getBairro());
        dto.getEndereco().setCidade(entity.getEndereco().getCidade());
        dto.getEndereco().setEstado(entity.getEndereco().getEstado());
        dto.getEndereco().setCep(entity.getEndereco().getCep());
        dto.getEndereco().setLatitude(entity.getEndereco().getLatitude());
        dto.getEndereco().setLongitude(entity.getEndereco().getLongitude());

        return dto;
    }
}