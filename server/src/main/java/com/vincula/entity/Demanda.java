package com.vincula.entity;

import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.MotivoComplemento;
import com.vincula.enums.PrazoDemanda;
import com.vincula.enums.Prioridade;
import com.vincula.enums.StatusDemanda;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "demanda")
@Data
public class Demanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 21)
    private MotivoBuscaAtiva motivoBuscaAtiva;

    @Enumerated(EnumType.STRING)
    @Column(length = 38)
    private MotivoComplemento motivoComplemento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Prioridade prioridade;

    @Column(length = 500)
    private String descricaoBusca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrazoDemanda prazoDemanda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusDemanda status;

    @Column(nullable = false)
    private LocalDateTime dataHoraLimite;

    @Column(nullable = false)
    private LocalDateTime dataHoraCriacao;

    private LocalDateTime dataHoraFinalizacao;

    private LocalDateTime dataHoraRedirecionamento;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DesfechoDemanda desfecho;

    @Column(length = 500)
    private String descricaoDesfecho;

    @Column(nullable = true)
    private Boolean foiRedirecionada = false;

    @Column(length = 500)
    private String motivoRedirecionamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_solicitante_id")
    private UnidadeSaude unidadeSolicitante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_responsavel_id", nullable = false)
    private UnidadeSaude unidadeResponsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_responsavel_anterior_id")
    private UnidadeSaude unidadeResponsavelAnterior;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servidor_criador_id", nullable = false)
    private Servidor servidorCriador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servidor_encerramento_id")
    private Servidor servidorEncerramento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servidor_redirecionamento_id")
    private Servidor servidorRedirecionamento;

}