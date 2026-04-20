package com.vincula.entity;

import com.vincula.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false, length = 255)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PerfilUsuario perfil;

    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_saude_id")
    private UnidadeSaude unidadeSaude;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Observacao> observacoes;
}