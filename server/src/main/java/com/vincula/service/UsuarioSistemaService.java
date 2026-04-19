package com.vincula.service;

import com.vincula.dto.UnidadeSaudeRefDTO;
import com.vincula.dto.UsuarioSistemaDTO;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.UsuarioSistema;
import com.vincula.enums.PerfilUsuario;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.repository.UsuarioSistemaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioSistemaService {

    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;

    public UsuarioSistemaService(UsuarioSistemaRepository usuarioSistemaRepository,
                                 UnidadeSaudeRepository unidadeSaudeRepository) {
        this.usuarioSistemaRepository = usuarioSistemaRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
    }

    public UsuarioSistemaDTO criar(UsuarioSistemaDTO dto) {
        UsuarioSistema entity = toEntity(dto);
        UsuarioSistema salvo = usuarioSistemaRepository.save(entity);

        return toDTO(salvo);
    }

    public List<UsuarioSistemaDTO> listarTodos() {
        return usuarioSistemaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UsuarioSistemaDTO buscarPorId(Long id) {
        UsuarioSistema entity = usuarioSistemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário do sistema não encontrado"));

        return toDTO(entity);
    }

    public UsuarioSistemaDTO buscarPorEmail(String email) {
        UsuarioSistema entity = usuarioSistemaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário do sistema não encontrado"));

        return toDTO(entity);
    }

    public UsuarioSistemaDTO buscarPorLogin(String login) {
        UsuarioSistema entity = usuarioSistemaRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuário do sistema não encontrado"));

        return toDTO(entity);
    }

    public UsuarioSistemaDTO atualizar(Long id, UsuarioSistemaDTO dto) {
        UsuarioSistema entity = usuarioSistemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário do sistema não encontrado"));

        validarDuplicidadeUpdate(dto, id);
        validarPerfilEUnidade(dto);

        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setLogin(dto.getLogin());
        entity.setSenha(dto.getSenha());
        entity.setPerfil(dto.getPerfil());
        entity.setAtivo(dto.getAtivo());

        if (dto.getUnidadeSaude() != null && dto.getUnidadeSaude().getId() != null) {
            UnidadeSaude unidade = unidadeSaudeRepository.findById(dto.getUnidadeSaude().getId())
                    .orElseThrow(() -> new RuntimeException("Unidade de saúde não encontrada"));
            entity.setUnidadeSaude(unidade);
        } else {
            entity.setUnidadeSaude(null);
        }

        UsuarioSistema atualizado = usuarioSistemaRepository.save(entity);
        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        UsuarioSistema entity = usuarioSistemaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário do sistema não encontrado"));

        usuarioSistemaRepository.delete(entity);
    }

    private void validarDuplicidadeCreate(UsuarioSistemaDTO dto) {
        if (usuarioSistemaRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (usuarioSistemaRepository.existsByLogin(dto.getLogin())) {
            throw new RuntimeException("Login já cadastrado");
        }
    }

    private void validarDuplicidadeUpdate(UsuarioSistemaDTO dto, Long id) {
        if (usuarioSistemaRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (usuarioSistemaRepository.existsByLoginAndIdNot(dto.getLogin(), id)) {
            throw new RuntimeException("Login já cadastrado");
        }
    }

    private void validarPerfilEUnidade(UsuarioSistemaDTO dto) {
        if (dto.getPerfil() == PerfilUsuario.EXECUTOR_APS) {
            if (dto.getUnidadeSaude() == null || dto.getUnidadeSaude().getId() == null) {
                throw new RuntimeException("Usuário executor APS deve estar vinculado a uma unidade de saúde");
            }
        }
    }

    private UsuarioSistema toEntity(UsuarioSistemaDTO dto) {
        validarDuplicidadeCreate(dto);
        validarPerfilEUnidade(dto);

        UsuarioSistema entity = new UsuarioSistema();

        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setLogin(dto.getLogin());
        entity.setSenha(dto.getSenha());
        entity.setPerfil(dto.getPerfil());
        entity.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

        if (dto.getUnidadeSaude() != null && dto.getUnidadeSaude().getId() != null) {
            UnidadeSaude unidade = unidadeSaudeRepository.findById(dto.getUnidadeSaude().getId())
                    .orElseThrow(() -> new RuntimeException("Unidade de saúde não encontrada"));
            entity.setUnidadeSaude(unidade);
        }

        return entity;
    }

    private UsuarioSistemaDTO toDTO(UsuarioSistema entity) {
        UsuarioSistemaDTO dto = new UsuarioSistemaDTO();

        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        dto.setLogin(entity.getLogin());
        dto.setSenha(null);
        dto.setPerfil(entity.getPerfil());
        dto.setAtivo(entity.getAtivo());

        if (entity.getUnidadeSaude() != null) {
            UnidadeSaudeRefDTO unidadeDTO = new UnidadeSaudeRefDTO();
            unidadeDTO.setId(entity.getUnidadeSaude().getId());
            dto.setUnidadeSaude(unidadeDTO);
        }

        return dto;
    }
}