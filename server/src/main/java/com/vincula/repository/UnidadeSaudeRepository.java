package com.vincula.repository;

import com.vincula.entity.UnidadeSaude;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnidadeSaudeRepository extends JpaRepository<UnidadeSaude, Long> {

    boolean existsByCnes(String cnes);

    boolean existsByCnesAndIdNot(String cnes, Long id);

    Optional<UnidadeSaude> findByCnes(String cnes);
}