package com.vincula.repository;

import com.vincula.entity.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<PacienteEntity, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCns(String cns);

    boolean existsByEndereco_Id(Long enderecoId);

    Optional<PacienteEntity> findByCpf(String cpf);

    Optional<PacienteEntity> findByCns(String cns);

    @Query("SELECT p FROM PacienteEntity p JOIN FETCH p.endereco WHERE p.id = :id")
    Optional<PacienteEntity> findByIdComEndereco(Long id);

    @Query("SELECT p FROM PacienteEntity p JOIN FETCH p.endereco WHERE p.cpf = :cpf")
    Optional<PacienteEntity> findByCpfComEndereco(String cpf);

    @Query("SELECT p FROM PacienteEntity p JOIN FETCH p.endereco WHERE p.cns = :cns")
    Optional<PacienteEntity> findByCnsComEndereco(String cns);

    @Query("SELECT p FROM PacienteEntity p JOIN FETCH p.endereco")
    List<PacienteEntity> findAllComEndereco();
}