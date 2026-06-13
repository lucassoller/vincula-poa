package com.vincula.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vincula.dto.TerritorioUbsDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.TerritorioUbs;
import com.vincula.entity.UnidadeSaude;
import com.vincula.repository.TerritorioUbsRepository;
import com.vincula.repository.UnidadeSaudeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    public void importar(JsonNode geojson) {

        try {
            JsonNode features = geojson.get("features");

            if (features == null || !features.isArray()) {
                throw new RuntimeException("GeoJSON inválido");
            }

            for (JsonNode feature : features) {

                JsonNode geometry = feature.get("geometry");

                if (geometry == null) {
                    continue;
                }

                String tipo = geometry.path("type").asText();

                if ("Point".equalsIgnoreCase(tipo)) {
                    importarPonto(feature);
                }
            }

            if (unidadeSaudeRepository.findByCnes("2264706").isEmpty()) {

                Endereco endereco = new Endereco();

                endereco.setRua("Rua K esquina Rua R C");

                endereco.setBairro("Santa Rosa de Lima");
                endereco.setCidade("Porto Alegre");
                endereco.setEstado("RS");
                endereco.setNumero("S/N");

                UnidadeSaude usRamos = new UnidadeSaude();
                usRamos.setNome("US Ramos");
                usRamos.setCnes("2264706");
                usRamos.setEndereco(endereco);
                unidadeSaudeRepository.save(usRamos);
            }

            for (JsonNode feature : features) {

                JsonNode geometry = feature.get("geometry");

                if (geometry == null) {
                    continue;
                }

                String tipo = geometry.path("type").asText();

                if ("Polygon".equalsIgnoreCase(tipo) || "MultiPolygon".equalsIgnoreCase(tipo) || "GeometryCollection".equalsIgnoreCase(tipo)) {
                    importarTerritorio(feature);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar mapa: " + e.getMessage(), e);
        }
    }

    private void importarPonto(JsonNode feature){

        JsonNode properties = feature.get("properties");
        JsonNode geometry = feature.get("geometry");

        if (properties == null || geometry == null) {
            return;
        }

        String nome = texto(properties, "name");
        String cnes = limparDecimal(texto(properties, "CNES"));

        if (cnes == null || cnes.isBlank()) {
            return;
        }

        JsonNode coordinates = geometry.get("coordinates");
        if (coordinates == null || coordinates.size() < 2) {
            return;
        }

        Double longitude = coordinates.get(0).asDouble();
        Double latitude = coordinates.get(1).asDouble();

        String enderecoTexto = texto(properties, "Endereço");

        String[] telefones = extrairTelefones(texto(properties, "Telefones"));

        UnidadeSaude unidade = unidadeSaudeRepository.findByCnes(cnes).orElseGet(UnidadeSaude::new);

        Endereco endereco = unidade.getEndereco();

        if (endereco == null) {
            endereco = new Endereco();
        }

        preencherEndereco(endereco, enderecoTexto, latitude, longitude);

        unidade.setNome(nome);
        unidade.setCnes(cnes);
        unidade.setEndereco(endereco);
        unidade.setTelefone(telefones[0]);
        unidade.setTelefone2(telefones[1]);

        unidadeSaudeRepository.save(unidade);
    }

    private void importarTerritorio(JsonNode feature) throws Exception {

        JsonNode properties = feature.get("properties");
        JsonNode geometry = feature.get("geometry");

        if (properties == null || geometry == null) {
            return;
        }

        String nome = texto(properties, "name");
        String cnes = limparDecimal(texto(properties, "CNES"));

        if (cnes == null || cnes.isBlank()) {
            return;
        }

        String distrito = texto(properties, "Distrito_S");

        TerritorioUbs territorio = territorioUbsRepository.findByCnes(cnes).orElseGet(TerritorioUbs::new);
        territorio.setNome(nome);
        territorio.setCnes(cnes);
        territorio.setDistrito(distrito);

        String tipo = geometry.path("type").asText();

        if ("GeometryCollection".equalsIgnoreCase(tipo)) {

            ArrayNode multiPolygonCoordinates = objectMapper.createArrayNode();

            JsonNode geometries = geometry.get("geometries");

            if (geometries != null && geometries.isArray()) {
                for (JsonNode geo : geometries) {
                    String geoTipo = geo.path("type").asText();

                    if ("Polygon".equalsIgnoreCase(geoTipo)) {
                        JsonNode coordinates = geo.get("coordinates");
                        multiPolygonCoordinates.add(coordinates);
                    } else if ("MultiPolygon".equalsIgnoreCase(geoTipo)) {

                        JsonNode coordinates = geo.get("coordinates");

                        if (coordinates != null && coordinates.isArray()) {

                            for (JsonNode polygon : coordinates) {
                                multiPolygonCoordinates.add(polygon);
                            }
                        }
                    }
                }
            }

            ObjectNode multiPolygon = objectMapper.createObjectNode();

            multiPolygon.put("type", "MultiPolygon");
            multiPolygon.set("coordinates", multiPolygonCoordinates);

            territorio.setGeojson(objectMapper.writeValueAsString(multiPolygon));

        } else {
            territorio.setGeojson(objectMapper.writeValueAsString(geometry));
        }

        UnidadeSaude unidade = unidadeSaudeRepository.findByCnes(cnes).orElse(null);

        territorio.setUnidadeSaude(unidade);
        territorioUbsRepository.save(territorio);
    }

    private String texto(JsonNode node, String campo) {
        JsonNode valor = node.get(campo);

        if (valor == null || valor.isNull()) {
            return null;
        }

        return valor.asText();
    }

    private String limparDecimal(String valor) {
        if (valor == null) {
            return null;
        }

        return valor.replace(".0", "").trim();
    }

    private void preencherEndereco(Endereco endereco, String enderecoTexto, Double latitude, Double longitude) {

        endereco.setCidade("Porto Alegre");
        endereco.setEstado("RS");
        endereco.setLatitude(latitude);
        endereco.setLongitude(longitude);

        if (enderecoTexto == null || enderecoTexto.isBlank()) {
            endereco.setRua("Não informado");
            endereco.setNumero("S/N");
            endereco.setBairro("Não informado");
            return;
        }

        String rua = enderecoTexto;
        String numero = "S/N";
        Pattern patternNumero = Pattern.compile("(?i)(?:número|nº|num|,)?\\s*(\\d+[A-Za-z]?)");

        Matcher matcher = patternNumero.matcher(enderecoTexto);

        String ultimoNumero = null;

        while (matcher.find()) {
            ultimoNumero = matcher.group(1);
        }

        if (ultimoNumero != null) {

            numero = ultimoNumero;

            rua = enderecoTexto.replaceFirst("(?i)(?:número|nº|num|,)?\\s*" + numero, "")
                    .replace("-", "")
                    .trim();
        }

        String bairro = "Não informado";

        Pattern patternBairro = Pattern.compile("(?i)bairro\\s+(.+)");

        Matcher matcherBairro = patternBairro.matcher(enderecoTexto);

        if (matcherBairro.find()) {

            bairro = matcherBairro.group(1).trim();

            rua = rua.replaceAll("(?i)-?\\s*bairro\\s+" + Pattern.quote(bairro), "")
                    .replaceAll("-$", "")
                    .replaceAll(",$", "")
                    .trim();
        }

        endereco.setRua(rua);
        endereco.setNumero(numero);
        endereco.setBairro(bairro);
    }

    private String[] extrairTelefones(String texto) {

        if (texto == null || texto.isBlank()) {
            return new String[]{null, null};
        }

        String limpo = texto.replace("(WhatsApp)", "").trim();

        String[] partes = limpo.split("/");

        String telefone1 = null;
        String telefone2 = null;

        if (partes.length > 0) {
            telefone1 = normalizarTelefone(partes[0]);
        }

        if (partes.length > 1) {
            telefone2 = normalizarTelefone(partes[1]);
        }

        return new String[]{telefone1, telefone2
        };
    }

    private String normalizarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }

        String apenasNumeros = telefone.replaceAll("[^0-9]", "");

        if (apenasNumeros.startsWith("55")) {
            apenasNumeros = apenasNumeros.substring(2);
        }

        if (!apenasNumeros.startsWith("51")) {
            apenasNumeros = "51" + apenasNumeros;
        }

        if (apenasNumeros.length() > 11) {
            apenasNumeros = apenasNumeros.substring(0, 11);
        }
        return apenasNumeros;
    }
}