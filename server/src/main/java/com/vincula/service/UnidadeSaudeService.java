package com.vincula.service;

import com.vincula.dto.EnderecoDTO;
import com.vincula.dto.UnidadeSaudeDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.UnidadeSaude;
import com.vincula.repository.UnidadeSaudeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnidadeSaudeService {

    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final EnderecoService enderecoService;

    public UnidadeSaudeService(UnidadeSaudeRepository unidadeSaudeRepository, EnderecoService enderecoService) {
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.enderecoService = enderecoService;
    }

    public UnidadeSaudeDTO criar(UnidadeSaudeDTO dto) {
        UnidadeSaude entity = toEntity(dto);

        UnidadeSaude salvo = unidadeSaudeRepository.save(entity);
        return toDTO(salvo);
    }

    public List<UnidadeSaudeDTO> listarTodos() {
        return unidadeSaudeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UnidadeSaudeDTO buscarPorId(Long id) {
        UnidadeSaude entity = unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidade de saúde não encontrada"));

        return toDTO(entity);
    }

    public UnidadeSaudeDTO buscarPorCnes(String cnes) {
        UnidadeSaude entity = unidadeSaudeRepository.findByCnes(cnes)
                .orElseThrow(() -> new RuntimeException("Unidade de saúde não encontrada"));

        return toDTO(entity);
    }

    public UnidadeSaudeDTO atualizar(Long id, UnidadeSaudeDTO dto) {
        UnidadeSaude entity = unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidade de saúde não encontrada"));

        validarCnesUpdate(dto, id);

        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());

        Endereco endereco = entity.getEndereco();

        endereco.setRua(dto.getEndereco().getRua());
        endereco.setNumero(dto.getEndereco().getNumero());
        endereco.setBairro(dto.getEndereco().getBairro());
        endereco.setCidade(dto.getEndereco().getCidade());
        endereco.setEstado(dto.getEndereco().getEstado());
        endereco.setCep(dto.getEndereco().getCep());
        endereco.setLatitude(dto.getEndereco().getLatitude());
        endereco.setLongitude(dto.getEndereco().getLongitude());

        UnidadeSaude atualizado = unidadeSaudeRepository.save(entity);

        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        UnidadeSaude entity = unidadeSaudeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidade de saúde não encontrada"));

        unidadeSaudeRepository.delete(entity);
    }

    private void validarCnesCreate(UnidadeSaudeDTO dto) {
        if (unidadeSaudeRepository.existsByCnes(dto.getCnes())) {
            throw new RuntimeException("CNES já cadastrado");
        }
    }

    private void validarCnesUpdate(UnidadeSaudeDTO dto, Long id) {
        if (unidadeSaudeRepository.existsByCnesAndIdNot(dto.getCnes(), id)) {
            throw new RuntimeException("CNES já cadastrado");
        }
    }

    private UnidadeSaude toEntity(UnidadeSaudeDTO dto){
        validarCnesCreate(dto);

        Endereco endereco = enderecoService.toEntity(dto.getEndereco());

        UnidadeSaude entity = new UnidadeSaude();
        entity.setNome(dto.getNome());
        entity.setCnes(dto.getCnes());
        entity.setEndereco(endereco);

        return entity;
    }

    private UnidadeSaudeDTO toDTO(UnidadeSaude entity) {
        UnidadeSaudeDTO dto = new UnidadeSaudeDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCnes(entity.getCnes());
        dto.setEndereco(enderecoService.toDTO(entity.getEndereco()));
        return dto;
    }
}