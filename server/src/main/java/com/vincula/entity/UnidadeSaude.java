package com.vincula.entity;

import com.vincula.enums.TipoServico;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "unidade_saude")
@Data
public class UnidadeSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 11)
    private String telefone;

    @Column(length = 11)
    private String telefone2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 21)
    private TipoServico tipoServico;

    @Column(nullable = false, unique = true, length = 7)
    private String cnes;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", nullable = false, unique = true)
    private Endereco endereco;

    @OneToMany(mappedBy = "unidadeSaude", fetch = FetchType.LAZY)
    private List<Usuario> usuarios;

    @OneToMany(mappedBy = "unidadeSaude", fetch = FetchType.LAZY)
    private List<TerritorioUbs> territorios;
}