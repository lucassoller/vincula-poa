package com.vincula.service;

import com.vincula.dto.UsuarioDTO;
import com.vincula.entity.UnidadeSaude;
import com.vincula.entity.Usuario;
import com.vincula.enums.PerfilUsuario;
import com.vincula.exception.BusinessException;
import com.vincula.exception.ConflictException;
import com.vincula.exception.NotFoundException;
import com.vincula.repository.UnidadeSaudeRepository;
import com.vincula.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          UnidadeSaudeRepository unidadeSaudeRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioDTO criar(UsuarioDTO dto) {
        Usuario entity = toEntity(dto);
        Usuario salvo = usuarioRepository.save(entity);

        return toDTO(salvo);
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO buscarPorId(Long id) {
        Usuario entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário do sistema não encontrado"));

        return toDTO(entity);
    }

    public UsuarioDTO buscarPorEmail(String email) {
        Usuario entity = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário do sistema não encontrado"));

        return toDTO(entity);
    }

    public UsuarioDTO buscarPorLogin(String login) {
        Usuario entity = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("Usuário do sistema não encontrado"));

        return toDTO(entity);
    }

    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        Usuario entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário do sistema não encontrado"));

        validarDuplicidadeUpdate(dto, id);
        validarPerfilEUnidade(dto);

        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setLogin(dto.getLogin());
        entity.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        entity.setPerfil(dto.getPerfil());
        entity.setAtivo(dto.getAtivo());

        if (dto.getUnidadeSaudeId() != null) {
            if(dto.getPerfil().equals(PerfilUsuario.SOLICITANTE) || dto.getPerfil().equals(PerfilUsuario.GESTAO_MUNICIPAL)){
                throw new BusinessException(dto.getPerfil() + " não deve estar vinculado a uma unidade de saúde");
            }
            UnidadeSaude unidade = unidadeSaudeRepository.findById(dto.getUnidadeSaudeId())
                    .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));
            entity.setUnidadeSaude(unidade);
        } else {
            if(dto.getPerfil().equals(PerfilUsuario.EXECUTOR_APS)){
                throw new BusinessException(dto.getPerfil() + " deve estar vinculado a uma unidade de saúde");
            }
            entity.setUnidadeSaude(null);
        }

        Usuario atualizado = usuarioRepository.save(entity);
        return toDTO(atualizado);
    }

    public void deletar(Long id) {
        Usuario entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário do sistema não encontrado"));

        usuarioRepository.delete(entity);
    }

    private void validarDuplicidadeCreate(UsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email já cadastrado");
        }

        if (usuarioRepository.existsByLogin(dto.getLogin())) {
            throw new ConflictException("Login já cadastrado");
        }
    }

    private void validarDuplicidadeUpdate(UsuarioDTO dto, Long id) {
        if (usuarioRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ConflictException("Email já cadastrado");
        }

        if (usuarioRepository.existsByLoginAndIdNot(dto.getLogin(), id)) {
            throw new ConflictException("Login já cadastrado");
        }
    }

    private void validarPerfilEUnidade(UsuarioDTO dto) {
        if (dto.getPerfil() == PerfilUsuario.EXECUTOR_APS) {
            if (dto.getUnidadeSaudeId() == null) {
                throw new BusinessException("Usuário executor APS deve estar vinculado a uma unidade de saúde");
            }
        }
    }

    public Usuario toEntity(UsuarioDTO dto) {
        validarDuplicidadeCreate(dto);
        validarPerfilEUnidade(dto);

        Usuario entity = new Usuario();

        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setLogin(dto.getLogin());
        entity.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        entity.setPerfil(dto.getPerfil());
        entity.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

        if (dto.getUnidadeSaudeId() != null) {
            if(dto.getPerfil().equals(PerfilUsuario.SOLICITANTE) || dto.getPerfil().equals(PerfilUsuario.GESTAO_MUNICIPAL)){
                throw new BusinessException(dto.getPerfil() + " não deve estar vinculado a uma unidade de saúde");
            }
            UnidadeSaude unidade = unidadeSaudeRepository.findById(dto.getUnidadeSaudeId())
                    .orElseThrow(() -> new NotFoundException("Unidade de saúde não encontrada"));
            entity.setUnidadeSaude(unidade);
        } else {
            if(dto.getPerfil().equals(PerfilUsuario.EXECUTOR_APS)){
                throw new BusinessException(dto.getPerfil() + " deve estar vinculado a uma unidade de saúde");
            }
            entity.setUnidadeSaude(null);
        }

        return entity;
    }

    public Usuario buscarUsuarioLogado() {
        String login = com.vincula.security.SecurityUtils.getLoginUsuarioLogado();

        if (login == null) {
            throw new BusinessException("Usuário não autenticado");
        }

        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado"));
    }

    public UsuarioDTO toDTO(Usuario entity) {
        UsuarioDTO dto = new UsuarioDTO();

        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        dto.setLogin(entity.getLogin());
        dto.setSenha(null);
        dto.setPerfil(entity.getPerfil());
        dto.setAtivo(entity.getAtivo());
        if (entity.getUnidadeSaude() != null) {
            dto.setUnidadeSaudeId(entity.getUnidadeSaude().getId());
        } else {
            dto.setUnidadeSaudeId(null);
        }

        return dto;
    }
}