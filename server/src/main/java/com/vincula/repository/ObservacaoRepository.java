package com.vincula.repository;

import com.vincula.entity.Observacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservacaoRepository extends JpaRepository<Observacao, Long> {

    List<Observacao> findByPacienteId(Long pacienteId);
    List<Observacao> findByUsuarioId(Long usuarioId);
}