package com.vincula.entity;

import com.vincula.enums.Sexo;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "paciente")
@Data
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nomeCompleto;

    @Column(length = 11)
    private String telefone;

    @Column()
    private LocalDateTime dataNascimento;

    @Column(length = 50)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 13)
    private Sexo sexo;

    @Column(unique = true, length = 11)
    private String cpf;

    @Column(unique = true, length = 15)
    private String cns;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", nullable = false, unique = true)
    private Endereco endereco;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_saude_id", nullable = false)
    private UnidadeSaude unidadeSaude;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY)
    private List<Observacao> observacoes;
}