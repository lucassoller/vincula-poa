package com.vincula.repository;

import com.vincula.entity.TerritorioUbs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TerritorioUbsRepository
        extends JpaRepository<TerritorioUbs, Long> {


    List<TerritorioUbs> findAllByCnesIn(Collection<String> cnes);

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE territorio_ubs
    SET geom = ST_Force2D(
        ST_SetSRID(
            ST_GeomFromGeoJSON(:geojson),
            4326
        )
    )
    WHERE cnes = :cnes
    """, nativeQuery = true)
    void atualizarGeom(
            @Param("cnes") String cnes,
            @Param("geojson") String geojson
    );

    @Query(value = """
    SELECT servico_id
    FROM territorio_ubs
    WHERE ST_Covers(
        geom,
        ST_SetSRID(
            ST_MakePoint(:longitude, :latitude),
            4326
        )
    )
    LIMIT 1
    """, nativeQuery = true)
    Optional<Long> buscarServicoIdPorCoordenada(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude
    );
}