package com.vincula.repository;

import com.vincula.entity.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    Page<Auditoria> findAllByOrderByDataHoraDesc(Pageable pageable);

    Page<Auditoria> findByUsuarioIdOrderByDataHoraDesc(Long usuarioId, Pageable pageable);

    Page<Auditoria> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    Page<Auditoria> findByUsuarioIdAndDataHoraBetweenOrderByDataHoraDesc(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fim,
            Pageable pageable
    );
}