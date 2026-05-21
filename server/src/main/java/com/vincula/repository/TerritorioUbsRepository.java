package com.vincula.repository;

import com.vincula.entity.TerritorioUbs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TerritorioUbsRepository
        extends JpaRepository<TerritorioUbs, Long> {

    Optional<TerritorioUbs> findByCnes(String cnes);
}