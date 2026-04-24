package com.vincula.entity;

import com.vincula.enums.TipoAcaoAuditoria;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoAcaoAuditoria acao;

    @Column(nullable = false, length = 100)
    private String entidade;

    @Column(nullable = false)
    private Long entidadeId;

    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}