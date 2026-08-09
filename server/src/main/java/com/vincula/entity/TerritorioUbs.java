package com.vincula.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "territorio_ubs")
@Data
public class TerritorioUbs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 7)
    private String cnes;

    @Column(nullable = false, length = 100)
    private String distrito;

    @Column(columnDefinition = "geometry(Geometry,4326)")
    private Geometry geom;

    @ManyToOne
    @JoinColumn(name = "servico_id")
    private Servico servico;
}