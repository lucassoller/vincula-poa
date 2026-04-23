package com.vincula.entity;

import com.vincula.enums.DesfechoDemanda;
import com.vincula.enums.MotivoBuscaAtiva;
import com.vincula.enums.PrazoDemanda;
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
    @Column(nullable = false, length = 14)
    private MotivoBuscaAtiva motivoBuscaAtiva;

    @Column(length = 500)
    private String descricaoBusca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrazoDemanda prazoDemanda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private StatusDemanda status;

    @Column(nullable = false)
    private LocalDateTime dataHoraCriacao;

    private LocalDateTime dataHoraFinalizacao;

    @Enumerated(EnumType.STRING)
    @Column(length = 18)
    private DesfechoDemanda desfecho;

    @Column(length = 500)
    private String descricaoDesfecho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_solicitante_id")
    private UnidadeSaude unidadeSolicitante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_responsavel_id", nullable = false)
    private UnidadeSaude unidadeResponsavel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_criador_id", nullable = false)
    private Usuario usuarioCriador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_encerramento_id")
    private Usuario usuarioEncerramento;

}