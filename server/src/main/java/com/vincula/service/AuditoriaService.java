package com.vincula.service;

import com.vincula.dto.auditoria.AuditoriaDTO;
import com.vincula.dto.auditoria.FiltroAuditoriaRequestDTO;
import com.vincula.entity.Auditoria;
import com.vincula.entity.Servidor;
import com.vincula.enums.TipoAcaoAuditoria;
import com.vincula.repository.AuditoriaRepository;
import com.vincula.repository.ServidorRepository;
import com.vincula.specification.AuditoriaSpecification;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final ServidorRepository servidorRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository,
                            ServidorRepository servidorRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.servidorRepository = servidorRepository;
    }

    public void registrar(TipoAcaoAuditoria acao,
                          String entidade,
                          Long entidadeId,
                          String descricao) {
        Servidor servidor = buscarServidorLogadoOuNull();

        Auditoria log = new Auditoria();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setDescricao(descricao);
        log.setDataHora(LocalDateTime.now());
        log.setServidor(servidor);
        log.setIp(obterIp());

        auditoriaRepository.save(log);
    }

    public void registrarComServidor(Servidor servidor,
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
        log.setServidor(servidor);
        log.setIp(obterIp());

        auditoriaRepository.save(log);
    }

    public Page<AuditoriaDTO> listarTodosFiltrados(
            FiltroAuditoriaRequestDTO filtro,
            Pageable pageable) {

        Specification<Auditoria> specification =
                AuditoriaSpecification.comFiltros(filtro);

        Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dataHora").descending()
        );

        return auditoriaRepository.findAll(specification, pageableOrdenado)
                .map(this::toDTO);
    }

    public AuditoriaDTO toDTO(Auditoria log) {
        return new AuditoriaDTO(
                log.getId(),
                log.getAcao(),
                log.getEntidade(),
                log.getEntidadeId(),
                log.getDescricao(),
                log.getDataHora(),
                log.getServidor() != null ? log.getServidor().getId() : null,
                log.getServidor() != null ? log.getServidor().getNome() : null,
                log.getIp()
        );
    }

    private Servidor buscarServidorLogadoOuNull() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            String login = authentication.getName();

            if (login == null || login.equals("anonymousUser")) {
                return null;
            }

            return servidorRepository.findByLogin(login).orElse(null);

        } catch (Exception e) {
            return null;
        }
    }

    private String obterIp() {
        try {
            ServletRequestAttributes attr =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attr == null) {
                return null;
            }

            HttpServletRequest request = attr.getRequest();

            String ip = request.getHeader("X-Forwarded-For");

            if (ip == null || ip.isBlank()) {
                ip = request.getRemoteAddr();
            }

            return ip;

        } catch (Exception e) {
            return null;
        }
    }
}