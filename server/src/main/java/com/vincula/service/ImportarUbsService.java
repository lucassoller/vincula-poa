package com.vincula.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.entity.Endereco;
import com.vincula.entity.UnidadeSaude;
import com.vincula.repository.UnidadeSaudeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImportarUbsService {

    private final UnidadeSaudeRepository unidadeSaudeRepository;
    private final ObjectMapper objectMapper;

    public ImportarUbsService(
            UnidadeSaudeRepository unidadeSaudeRepository,
            ObjectMapper objectMapper
    ) {
        this.unidadeSaudeRepository = unidadeSaudeRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void importar() {

        try {

            InputStream inputStream = getClass()
                    .getResourceAsStream("/geojson/unidades-pontos.geojson");

            if (inputStream == null) {
                throw new RuntimeException("Arquivo de pontos não encontrado.");
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
                String cnes = limparDecimal(texto(properties, "CNES"));

                if (cnes == null || cnes.isBlank()) {
                    continue;
                }

                JsonNode coordinates = geometry.get("coordinates");

                if (
                        coordinates == null ||
                                !coordinates.isArray() ||
                                coordinates.size() < 2
                ) {
                    continue;
                }

                Double longitude = coordinates.get(0).asDouble();
                Double latitude = coordinates.get(1).asDouble();

                Optional<UnidadeSaude> unidadeOptional =
                        unidadeSaudeRepository.findByCnes(cnes);

                UnidadeSaude unidade;
                unidade = unidadeOptional.orElseGet(UnidadeSaude::new);

                unidade.setNome(nome);
                unidade.setCnes(cnes);
                String enderecoTexto =
                        texto(properties, "Endereço");

                Endereco endereco = montarEndereco(
                        enderecoTexto,
                        latitude,
                        longitude
                );

                unidade.setEndereco(endereco);

                String[] telefones =
                        extrairTelefones(
                                texto(properties, "Telefones")
                        );

                unidade.setTelefone(telefones[0]);
                unidade.setTelefone2(telefones[1]);

                unidadeSaudeRepository.save(unidade);
            }

            // MANUAL NAO TEM NO GEOSAUDE
            Endereco endereco = new Endereco();
            endereco.setRua("Rua K esquina Rua R C");
            endereco.setBairro("Santa Rosa de Lima");
            endereco.setCidade("Porto Alegre");
            endereco.setEstado("RS");
            endereco.setNumero("S/N");
            endereco.setCep("00000000");

            UnidadeSaude usRamos = new UnidadeSaude();
            usRamos.setNome("US Ramos");
            usRamos.setCnes("2264706");
            usRamos.setEndereco(endereco);

            unidadeSaudeRepository.save(usRamos);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao importar UBS: " + e.getMessage(),
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

    private String limparDecimal(String valor) {

        if (valor == null) {
            return null;
        }

        return valor.replace(".0", "").trim();
    }

    private Endereco montarEndereco(
            String enderecoTexto,
            Double latitude,
            Double longitude
    ) {

        Endereco endereco = new Endereco();

        endereco.setCidade("Porto Alegre");
        endereco.setEstado("RS");
        endereco.setCep("00000000");
        endereco.setLatitude(latitude);
        endereco.setLongitude(longitude);

        if (enderecoTexto == null || enderecoTexto.isBlank()) {

            endereco.setRua("Não informado");
            endereco.setNumero("S/N");
            endereco.setBairro("Não informado");

            return endereco;
        }

        String rua = enderecoTexto;
        String numero = "S/N";

        Pattern patternNumero =
                Pattern.compile(
                        "(?i)(?:número|nº|num|,)?\\s*(\\d+[A-Za-z]?)"
                );

        Matcher matcher = patternNumero.matcher(enderecoTexto);

        String ultimoNumero = null;

        while (matcher.find()) {
            ultimoNumero = matcher.group(1);
        }

        if (ultimoNumero != null) {

            numero = ultimoNumero;

            rua = enderecoTexto
                    .replaceFirst(
                            "(?i)(?:número|nº|num|,)?\\s*" + numero,
                            ""
                    )
                    .replace("-", "")
                    .trim();
        }

        String bairro = "Não informado";

        Pattern patternBairro =
                Pattern.compile("(?i)bairro\\s+(.+)");

        Matcher matcherBairro =
                patternBairro.matcher(enderecoTexto);

        if (matcherBairro.find()) {
            bairro = matcherBairro.group(1).trim();
            rua = rua
                    .replaceAll("(?i)-?\\s*bairro\\s+" + Pattern.quote(bairro), "")
                    .replaceAll("-$", "")
                    .replaceAll(",$", "")
                    .trim();
        }

        endereco.setRua(rua);
        endereco.setNumero(numero);
        endereco.setBairro(bairro);

        return endereco;
    }

    private String[] extrairTelefones(String texto) {

        if (texto == null || texto.isBlank()) {
            return new String[]{null, null};
        }

        String limpo = texto
                .replace("(WhatsApp)", "")
                .trim();

        String[] partes = limpo.split("/");

        String telefone1 = null;
        String telefone2 = null;

        if (partes.length > 0) {
            telefone1 =
                    normalizarTelefone(partes[0]);
        }

        if (partes.length > 1) {
            telefone2 =
                    normalizarTelefone(partes[1]);
        }

        return new String[]{telefone1, telefone2};
    }

    private String normalizarTelefone(String telefone) {

        if (telefone == null || telefone.isBlank()) {
            return null;
        }

        String apenasNumeros =
                telefone.replaceAll("[^0-9]", "");

        if (apenasNumeros.startsWith("55")) {
            apenasNumeros = apenasNumeros.substring(2);
        }

        if (!apenasNumeros.startsWith("51")) {
            apenasNumeros = "51" + apenasNumeros;
        }

        if (apenasNumeros.length() > 11) {
            apenasNumeros =
                    apenasNumeros.substring(0, 11);
        }

        return apenasNumeros;
    }
}