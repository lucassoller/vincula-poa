package com.vincula.repository;

import com.vincula.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByCpf(String cpf);

    boolean existsByCns(String cns);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByCnsAndIdNot(String cns, Long id);

    Optional<Paciente> findByCpf(String cpf);

    Optional<Paciente> findByCns(String cns);

}