package com.vincula.service;

import com.vincula.entity.TerritorioUbs;
import com.vincula.entity.UnidadeSaude;
import com.vincula.repository.TerritorioUbsRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TerritorializacaoService {

    private final TerritorioUbsRepository repository;

    public UnidadeSaude buscarUbsPorCoordenada(
            Double latitude,
            Double longitude
    ) {

        GeometryFactory geometryFactory = new GeometryFactory();

        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        GeoJsonReader reader = new GeoJsonReader();

        List<TerritorioUbs> territorios = repository.findAll();

        for (TerritorioUbs territorio : territorios) {
            try {
                Geometry geometry = reader.read(territorio.getGeojson());

                if (geometry.contains(point)) {
                    return territorio.getUnidadeSaude();
                }

            } catch (Exception ignored) {
            }
        }

        return null;
    }
}