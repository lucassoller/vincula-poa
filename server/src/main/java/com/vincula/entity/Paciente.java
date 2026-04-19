package com.vincula.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "paciente")
@Data
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String sobrenome;

    @Column(length = 11)
    private String telefone;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true, length = 15)
    private String cns;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", nullable = false, unique = true)
    private Endereco endereco;
}