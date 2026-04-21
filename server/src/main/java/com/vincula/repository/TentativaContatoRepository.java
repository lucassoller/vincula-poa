package com.vincula.repository;

import com.vincula.entity.TentativaContato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TentativaContatoRepository extends JpaRepository<TentativaContato, Long> {

    List<TentativaContato> findByDemandaId(Long demandaId);
    List<TentativaContato> findByUsuario(Long usuarioId);
}