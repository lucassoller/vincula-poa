package com.vincula.service;

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

    public PacienteService(PacienteRepository pacienteRepository, EnderecoRepository enderecoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.enderecoRepository = enderecoRepository;
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

    public List<PacienteDTO> listarTodosComEndereco() {
        return pacienteRepository.findAllComEndereco()
                .stream()
                .map(this::toDTOComEnd)
                .collect(Collectors.toList());
    }

    public PacienteDTO buscarPorIdComEndereco(Long id) {
        PacienteEntity paciente = pacienteRepository.findByIdComEndereco(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        return toDTOComEnd(paciente);
    }

    public PacienteDTO buscarPorCpfComEndereco(String cpf) {
        PacienteEntity paciente = pacienteRepository.findByCpfComEndereco(cpf)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o CPF informado"));

        return toDTOComEnd(paciente);
    }

    public PacienteDTO buscarPorCnsComEndereco(String cns) {
        PacienteEntity paciente = pacienteRepository.findByCnsComEndereco(cns)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o CNS informado"));

        return toDTOComEnd(paciente);
    }

    public PacienteDTO atualizar(Long id, PacienteDTO dto) {
        PacienteEntity paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        if (!paciente.getCpf().equals(dto.getCpf()) && pacienteRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (!paciente.getCns().equals(dto.getCns()) && pacienteRepository.existsByCns(dto.getCns())) {
            throw new RuntimeException("CNS já cadastrado");
        }

        EnderecoEntity enderecoAntigo = paciente.getEndereco();
        EnderecoEntity novoEndereco = resolverEndereco(dto);

        paciente.setNome(dto.getNome());
        paciente.setSobrenome(dto.getSobrenome());
        paciente.setTelefone(dto.getTelefone());
        paciente.setCpf(dto.getCpf());
        paciente.setCns(dto.getCns());
        paciente.setEndereco(novoEndereco);

        PacienteEntity atualizado = pacienteRepository.save(paciente);

        limparEnderecoOrfao(enderecoAntigo);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        PacienteEntity paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        EnderecoEntity endereco = paciente.getEndereco();

        pacienteRepository.delete(paciente);

        limparEnderecoOrfao(endereco);
    }

    private void validarCpfECns(PacienteDTO dto) {
        if (pacienteRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (pacienteRepository.existsByCns(dto.getCns())) {
            throw new RuntimeException("CNS já cadastrado");
        }
    }

    private EnderecoEntity resolverEndereco(PacienteDTO dto) {
        if (dto.getEndereco() == null) {
            throw new RuntimeException("Endereço é obrigatório");
        }

        Long enderecoId = dto.getEndereco().getId();

        if (enderecoId != null && enderecoId > 0) {
            return enderecoRepository.findById(enderecoId)
                    .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        }

        EnderecoEntity novoEndereco = new EnderecoEntity();
        novoEndereco.setRua(dto.getEndereco().getRua());
        novoEndereco.setNumero(dto.getEndereco().getNumero());
        novoEndereco.setBairro(dto.getEndereco().getBairro());
        novoEndereco.setCidade(dto.getEndereco().getCidade());
        novoEndereco.setEstado(dto.getEndereco().getEstado());
        novoEndereco.setCep(dto.getEndereco().getCep());
        novoEndereco.setLatitude(dto.getEndereco().getLatitude());
        novoEndereco.setLongitude(dto.getEndereco().getLongitude());

        return enderecoRepository.save(novoEndereco);
    }

    private void limparEnderecoOrfao(EnderecoEntity endereco) {
        if (endereco == null || endereco.getId() == null) {
            return;
        }

        boolean aindaEmUso = pacienteRepository.existsByEndereco_Id(endereco.getId());

        if (!aindaEmUso) {
            enderecoRepository.deleteById(endereco.getId());
        }
    }

    private PacienteEntity toEntity(PacienteDTO dto) {
        validarCpfECns(dto);

        EnderecoEntity endereco = resolverEndereco(dto);

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

        return dto;
    }

    private PacienteDTO toDTOComEnd(PacienteEntity entity) {

        PacienteDTO dto = toDTO(entity);

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