package com.vincula.entity;

import com.vincula.enums.PerfilUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "usuario_sistema")
@Data
public class UsuarioSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false, length = 50)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PerfilUsuario perfil;

    @Column(nullable = false)
    private Boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_saude_id")
    private UnidadeSaude unidadeSaude;
}