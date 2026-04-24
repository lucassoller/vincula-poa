package com.vincula.service;

import com.vincula.dto.AuditoriaDTO;
import com.vincula.entity.Auditoria;
import com.vincula.entity.Usuario;
import com.vincula.enums.TipoAcaoAuditoria;
import com.vincula.repository.AuditoriaRepository;
import com.vincula.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository,
                            UsuarioRepository usuarioRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void registrar(TipoAcaoAuditoria acao,
                          String entidade,
                          Long entidadeId,
                          String descricao) {
        Usuario usuario = buscarUsuarioLogadoOuNull();

        Auditoria log = new Auditoria();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setDescricao(descricao);
        log.setDataHora(LocalDateTime.now());
        log.setUsuario(usuario);

        auditoriaRepository.save(log);
    }

    public void registrarComUsuario(Usuario usuario,
                                    TipoAcaoAuditoria acao,
                                    String entidade,
                                    Long entidadeId,
                                    String descricao) {

        Auditoria log = new Auditoria();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setDescricao(descricao);
        log.setDataHora(LocalDateTime.now());
        log.setUsuario(usuario);

        auditoriaRepository.save(log);
    }

    public List<AuditoriaDTO> listarTodos() {
        return auditoriaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public AuditoriaDTO toDTO(Auditoria log) {
        return new AuditoriaDTO(
                log.getId(),
                log.getAcao(),
                log.getEntidade(),
                log.getEntidadeId(),
                log.getDescricao(),
                log.getDataHora(),
                log.getUsuario() != null ? log.getUsuario().getId() : null,
                log.getUsuario() != null ? log.getUsuario().getNome() : null
        );
    }

    private Usuario buscarUsuarioLogadoOuNull() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            String login = authentication.getName();

            if (login == null || login.equals("anonymousUser")) {
                return null;
            }

            return usuarioRepository.findByLogin(login).orElse(null);

        } catch (Exception e) {
            return null;
        }
    }
}