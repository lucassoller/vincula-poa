package com.vincula.repository;

import com.vincula.entity.Auditoria;
import com.vincula.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> , JpaSpecificationExecutor<Auditoria> {
    Page<Auditoria> findAllByOrderByDataHoraDesc(Pageable pageable);

    Page<Auditoria> findByServidorIdOrderByDataHoraDesc(Long servidorId, Pageable pageable);

    Page<Auditoria> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    Page<Auditoria> findByServidorIdAndDataHoraBetweenOrderByDataHoraDesc(
            Long servidorId,
            LocalDateTime inicio,
            LocalDateTime fim,
            Pageable pageable
    );
}