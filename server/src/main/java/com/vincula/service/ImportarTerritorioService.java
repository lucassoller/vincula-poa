package com.vincula.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.TerritorioUbsDTO;
import com.vincula.entity.TerritorioUbs;
import com.vincula.entity.UnidadeSaude;
import com.vincula.repository.TerritorioUbsRepository;
import com.vincula.repository.UnidadeSaudeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;


@Service
public class ImportarTerritorioService {

    private final TerritorioUbsRepository territorioUbsRepository;
    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final ObjectMapper objectMapper;

    public ImportarTerritorioService(
            TerritorioUbsRepository territorioUbsRepository,
            UnidadeSaudeRepository unidadeSaudeRepository,
            ObjectMapper objectMapper
    ) {
        this.territorioUbsRepository = territorioUbsRepository;
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.objectMapper = objectMapper;
    }

    public List<TerritorioUbsDTO> listarTodos() {
        return territorioUbsRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private TerritorioUbsDTO toDTO(TerritorioUbs territorioUbs) {
        TerritorioUbsDTO dto = new TerritorioUbsDTO();

        dto.setNome(territorioUbs.getNome());
        dto.setCnes(territorioUbs.getCnes());
        dto.setDistrito(territorioUbs.getDistrito());
        dto.setGeojson(territorioUbs.getGeojson());
        dto.setTelefone(territorioUbs.getUnidadeSaude().getTelefone());
        dto.setTelefone2(territorioUbs.getUnidadeSaude().getTelefone2());
        dto.setEndereco(territorioUbs.getUnidadeSaude().getEndereco());

        return dto;
    }

    @Transactional
    public void importar() {

        try {

            InputStream inputStream = getClass()
                    .getResourceAsStream("/geojson/territorios-ubs.geojson");

            if (inputStream == null) {
                throw new RuntimeException("Arquivo GeoJSON não encontrado.");
            }

            JsonNode root = objectMapper.readTree(inputStream);

            JsonNode features = root.get("features");

            if (features == null || !features.isArray()) {
                throw new RuntimeException("GeoJSON inválido.");
            }

            for (JsonNode feature : features) {

                JsonNode properties = feature.get("properties");
                JsonNode geometry = feature.get("geometry");

                if (properties == null || geometry == null) {
                    continue;
                }

                String nome = texto(properties, "name");
                String cnes = texto(properties, "CNES");
                String distrito = texto(properties, "Distrito_S");

                String geoJson = objectMapper.writeValueAsString(geometry);

                TerritorioUbs territorio = new TerritorioUbs();

                territorio.setNome(nome);
                territorio.setCnes(cnes);
                territorio.setDistrito(distrito);
                territorio.setGeojson(geoJson);

                if (cnes != null && !cnes.isBlank()) {

                    Optional<UnidadeSaude> unidadeOptional =
                            unidadeSaudeRepository.findByCnes(cnes);

                    unidadeOptional.ifPresent(territorio::setUnidadeSaude);
                }

                territorioUbsRepository.save(territorio);
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Erro ao importar GeoJSON: " + e.getMessage(),
                    e
            );
        }
    }

    private String texto(JsonNode node, String campo) {

        JsonNode valor = node.get(campo);

        if (valor == null || valor.isNull()) {
            return null;
        }

        return valor.asText();
    }
}