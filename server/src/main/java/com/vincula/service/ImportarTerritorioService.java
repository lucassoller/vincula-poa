package com.vincula.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.TerritorioUbsDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.TerritorioUbs;
import com.vincula.entity.Servico;
import com.vincula.enums.TipoServico;
import com.vincula.repository.TerritorioUbsRepository;
import com.vincula.repository.ServicoRepository;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImportarTerritorioService {

    private final TerritorioUbsRepository territorioUbsRepository;
    private final ServicoRepository servicoRepository;
    private final ObjectMapper objectMapper;

    public ImportarTerritorioService(
            TerritorioUbsRepository territorioUbsRepository,
            ServicoRepository servicoRepository,
            ObjectMapper objectMapper
    ) {
        this.territorioUbsRepository = territorioUbsRepository;
        this.servicoRepository = servicoRepository;
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
        dto.setTelefone(territorioUbs.getServico().getTelefone());
        dto.setTelefone2(territorioUbs.getServico().getTelefone2());
        dto.setEndereco(territorioUbs.getServico().getEndereco());
        dto.setGeom(converterGeomParaGeoJson(territorioUbs.getGeom()));

        return dto;
    }

    private String converterGeomParaGeoJson(Geometry geom) {
        if (geom == null) {
            return null;
        }

        GeoJsonWriter writer = new GeoJsonWriter();
        return writer.write(geom);
    }

    @Transactional
    public void importar(JsonNode geojson) {
        try {
            JsonNode features = geojson.get("features");

            if (features == null || !features.isArray()) {
                throw new RuntimeException("GeoJSON inválido");
            }

            Map<String, Servico> servicosPorCnes = new HashMap<>();
            Map<String, TerritorioUbs> territoriosPorCnes = new HashMap<>();

            servicosPorCnes.put("2264706", null);

            for (JsonNode feature : features) {

                JsonNode properties = feature.get("properties");
                JsonNode geometry = feature.get("geometry");

                if (properties == null || geometry == null) {
                    continue;
                }

                String cnes = limparDecimal(
                        texto(properties, "CNES")
                );

                if (cnes == null || cnes.isBlank()) {
                    continue;
                }

                String tipo = geometry.path("type").asText();

                if ("Point".equalsIgnoreCase(tipo)) {
                    servicosPorCnes.putIfAbsent(cnes, null);
                }

                if ("Polygon".equalsIgnoreCase(tipo)
                        || "MultiPolygon".equalsIgnoreCase(tipo)
                        || "GeometryCollection".equalsIgnoreCase(tipo)) {

                    territoriosPorCnes.putIfAbsent(cnes, null);
                }
            }

            if (!servicosPorCnes.isEmpty()) {

                List<Servico> servicosExistentes =
                        servicoRepository.findAllByCnesIn(
                                servicosPorCnes.keySet()
                        );

                for (Servico servico : servicosExistentes) {
                    servicosPorCnes.put(
                            servico.getCnes(),
                            servico
                    );
                }
            }

            if (!territoriosPorCnes.isEmpty()) {

                List<TerritorioUbs> territoriosExistentes =
                        territorioUbsRepository.findAllByCnesIn(
                                territoriosPorCnes.keySet()
                        );

                for (TerritorioUbs territorio : territoriosExistentes) {
                    territoriosPorCnes.put(
                            territorio.getCnes(),
                            territorio
                    );
                }
            }

            for (JsonNode feature : features) {

                JsonNode geometry = feature.get("geometry");

                if (geometry == null) {
                    continue;
                }

                if ("Point".equalsIgnoreCase(
                        geometry.path("type").asText())) {

                    importarPonto(
                            feature,
                            servicosPorCnes
                    );
                }
            }

            if (servicosPorCnes.get("2264706") == null) {

                Endereco endereco = new Endereco();

                endereco.setRua("Rua K esquina Rua R C");
                endereco.setBairro("Santa Rosa de Lima");
                endereco.setCidade("Porto Alegre");
                endereco.setEstado("RS");
                endereco.setNumero("S/N");

                Servico usRamos = new Servico();

                usRamos.setNome("US Ramos");
                usRamos.setCnes("2264706");
                usRamos.setTipoServico(TipoServico.UBS);
                usRamos.setEndereco(endereco);

                servicosPorCnes.put(
                        "2264706",
                        usRamos
                );
            }

            servicoRepository.saveAll(
                    servicosPorCnes.values()
            );

            for (JsonNode feature : features) {

                JsonNode geometry = feature.get("geometry");

                if (geometry == null) {
                    continue;
                }

                String tipo = geometry.path("type").asText();

                if ("Polygon".equalsIgnoreCase(tipo)
                        || "MultiPolygon".equalsIgnoreCase(tipo)
                        || "GeometryCollection".equalsIgnoreCase(tipo)) {

                    importarTerritorio(
                            feature,
                            territoriosPorCnes,
                            servicosPorCnes
                    );
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao importar mapa: " + e.getMessage(),
                    e
            );
        }
    }

    private void importarPonto(
            JsonNode feature,
            Map<String, Servico> servicosPorCnes
    ) {

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

        String[] telefones = extrairTelefones(
                texto(properties, "Telefones")
        );

        Servico servico = servicosPorCnes.get(cnes);

        if (servico == null) {
            servico = new Servico();
        }

        Endereco endereco = servico.getEndereco();

        if (endereco == null) {
            endereco = new Endereco();
        }

        preencherEndereco(
                endereco,
                enderecoTexto,
                latitude,
                longitude
        );

        servico.setNome(nome);
        servico.setCnes(cnes);
        servico.setEndereco(endereco);
        servico.setTelefone(telefones[0]);
        servico.setTelefone2(telefones[1]);
        servico.setTipoServico(TipoServico.UBS);

        servicosPorCnes.put(cnes, servico);
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

    private void importarTerritorio(
            JsonNode feature,
            Map<String, TerritorioUbs> territoriosPorCnes,
            Map<String, Servico> servicosPorCnes
    ) throws Exception {

        JsonNode properties = feature.get("properties");
        JsonNode geometry = feature.get("geometry");

        if (properties == null || geometry == null) {
            return;
        }

        String nome = texto(properties, "name");
        String cnes = limparDecimal(texto(properties, "CNES"));
        String distrito = texto(properties, "Distrito_S");

        if (cnes == null || cnes.isBlank()) {
            return;
        }

        TerritorioUbs territorio = territoriosPorCnes.get(cnes);

        if (territorio == null) {
            territorio = new TerritorioUbs();
        }

        territorio.setNome(nome);
        territorio.setCnes(cnes);
        territorio.setDistrito(distrito);

        Servico servico = servicosPorCnes.get(cnes);
        territorio.setServico(servico);

        territoriosPorCnes.put(cnes, territorio);

        territorioUbsRepository.save(territorio);

        territorioUbsRepository.atualizarGeom(
                cnes,
                objectMapper.writeValueAsString(geometry)
        );
    }

}