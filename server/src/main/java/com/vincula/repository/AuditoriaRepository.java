package com.vincula.repository;

import com.vincula.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByUsuarioIdOrderByDataHoraDesc(Long usuarioId);

    List<Auditoria> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime inicio, LocalDateTime fim);

    List<Auditoria> findByUsuarioIdAndDataHoraBetweenOrderByDataHoraDesc(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fim
    );
}