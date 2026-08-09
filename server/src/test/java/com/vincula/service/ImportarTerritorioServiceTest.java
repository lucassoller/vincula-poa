package com.vincula.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vincula.dto.TerritorioUbsDTO;
import com.vincula.entity.Endereco;
import com.vincula.entity.TerritorioUbs;
import com.vincula.entity.Servico;
import com.vincula.repository.TerritorioUbsRepository;
import com.vincula.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportarTerritorioServiceTest {

    @Mock
    private TerritorioUbsRepository territorioUbsRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();

        service = new ImportarTerritorioService(
                territorioUbsRepository,
                servicoRepository,
                objectMapper
        );
    }

    @InjectMocks
    private ImportarTerritorioService service;

    @Test
    void deveListarTodosTerritorios() {

        Servico servico = new Servico();
        servico.setTelefone("51999999999");
        servico.setTelefone2("51888888888");

        Endereco endereco = new Endereco();
        servico.setEndereco(endereco);

        TerritorioUbs territorio = new TerritorioUbs();
        territorio.setNome("Território A");
        territorio.setCnes("123");
        territorio.setDistrito("Norte");
        territorio.setServico(servico);

        when(territorioUbsRepository.findAll())
                .thenReturn(List.of(territorio));

        List<TerritorioUbsDTO> resultado = service.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Território A", resultado.get(0).getNome());
        assertEquals("123", resultado.get(0).getCnes());
    }

    @Test
    void deveLancarErroQuandoGeoJsonInvalido() {

        ObjectNode geojson = objectMapper.createObjectNode();

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.importar(geojson)
        );

        assertTrue(ex.getMessage().contains("GeoJSON inválido"));
    }

    @Test
    void deveIgnorarFeatureSemGeometry() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        features.add(feature);
        geojson.set("features", features);

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.of(new Servico()));

        service.importar(geojson);

        verify(servicoRepository, never()).save(any());
    }

    @Test
    void deveImportarPonto() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("name", "UBS TESTE");
        properties.put("CNES", "12345.0");
        properties.put("Endereço", "Rua Flores 100 Bairro Centro");
        properties.put("Telefones", "(51)99999-9999");

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Point");

        ArrayNode coordinates = objectMapper.createArrayNode();
        coordinates.add(-51.0);
        coordinates.add(-30.0);

        geometry.set("coordinates", coordinates);

        feature.set("properties", properties);
        feature.set("geometry", geometry);

        features.add(feature);
        geojson.set("features", features);

        when(servicoRepository.findByCnes("12345"))
                .thenReturn(Optional.empty());

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.of(new Servico()));

        service.importar(geojson);

        verify(servicoRepository, atLeastOnce())
                .save(any(Servico.class));
    }

    @Test
    void deveIgnorarPontoSemCnes() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode props = objectMapper.createObjectNode();
        props.put("name", "UBS");

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Point");

        feature.set("properties", props);
        feature.set("geometry", geometry);

        features.add(feature);
        geojson.set("features", features);

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.of(new Servico()));

        service.importar(geojson);

        verify(servicoRepository, never())
                .save(argThat(u -> "UBS".equals(u.getNome())));
    }

    @Test
    void deveCriarUsRamosQuandoNaoExistir() {

        ObjectNode geojson = objectMapper.createObjectNode();
        geojson.set("features", objectMapper.createArrayNode());

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.empty());

        service.importar(geojson);

        verify(servicoRepository)
                .save(argThat(u ->
                        "2264706".equals(u.getCnes())
                ));
    }

    @Test
    void deveImportarTerritorioPolygon() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("name", "Território");
        properties.put("CNES", "123");
        properties.put("Distrito_S", "Centro");

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Polygon");
        geometry.set("coordinates", objectMapper.createArrayNode());

        feature.set("properties", properties);
        feature.set("geometry", geometry);

        features.add(feature);
        geojson.set("features", features);

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.of(new Servico()));

        when(servicoRepository.findByCnes("123"))
                .thenReturn(Optional.of(new Servico()));

        service.importar(geojson);

        verify(territorioUbsRepository)
                .save(any(TerritorioUbs.class));
    }

    @Test
    void deveImportarPontoComEnderecoETelefoneVazios() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("name", "UBS TESTE");
        properties.put("CNES", "123");

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Point");

        ArrayNode coordinates = objectMapper.createArrayNode();
        coordinates.add(-51.0);
        coordinates.add(-30.0);

        geometry.set("coordinates", coordinates);

        feature.set("properties", properties);
        feature.set("geometry", geometry);

        features.add(feature);
        geojson.set("features", features);

        when(servicoRepository.findByCnes("123"))
                .thenReturn(Optional.empty());

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.of(new Servico()));

        service.importar(geojson);

        verify(servicoRepository, atLeastOnce())
                .save(argThat(u ->
                        "Não informado".equals(u.getEndereco().getRua())
                                && "S/N".equals(u.getEndereco().getNumero())
                                && "Não informado".equals(u.getEndereco().getBairro())
                ));
    }

    @Test
    void deveImportarDoisTelefones() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("name", "UBS TESTE");
        properties.put("CNES", "123");
        properties.put(
                "Telefones",
                "+55 (51) 99999-9999 / 98444-1111"
        );

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Point");

        ArrayNode coordinates = objectMapper.createArrayNode();
        coordinates.add(-51.0);
        coordinates.add(-30.0);

        geometry.set("coordinates", coordinates);

        feature.set("properties", properties);
        feature.set("geometry", geometry);

        features.add(feature);
        geojson.set("features", features);

        when(servicoRepository.findByCnes("123"))
                .thenReturn(Optional.empty());

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.of(new Servico()));

        service.importar(geojson);

        verify(servicoRepository).save(
                argThat(u ->
                        "51999999999".equals(u.getTelefone())
                                && "51984441111".equals(u.getTelefone2())
                )
        );
    }

    @Test
    void deveAdicionarDddQuandoNaoExistir() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("name", "UBS TESTE");
        properties.put("CNES", "123");
        properties.put("Telefones", "9999911111");

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Point");

        ArrayNode coordinates = objectMapper.createArrayNode();
        coordinates.add(-51.0);
        coordinates.add(-30.0);

        geometry.set("coordinates", coordinates);

        feature.set("properties", properties);
        feature.set("geometry", geometry);

        features.add(feature);
        geojson.set("features", features);

        when(servicoRepository.findByCnes("123"))
                .thenReturn(Optional.empty());

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.of(new Servico()));

        service.importar(geojson);

        verify(servicoRepository).save(
                argThat(u ->
                        "51999991111".equals(u.getTelefone())
                )
        );
    }

    @Test
    void deveLimitarTelefoneParaOnzeDigitos() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("name", "UBS TESTE");
        properties.put("CNES", "123");
        properties.put("Telefones", "519999999999999999");

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Point");

        ArrayNode coordinates = objectMapper.createArrayNode();
        coordinates.add(-51.0);
        coordinates.add(-30.0);

        geometry.set("coordinates", coordinates);

        feature.set("properties", properties);
        feature.set("geometry", geometry);

        features.add(feature);
        geojson.set("features", features);

        when(servicoRepository.findByCnes("123"))
                .thenReturn(Optional.empty());

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.of(new Servico()));

        service.importar(geojson);

        verify(servicoRepository).save(
                argThat(u ->
                        u.getTelefone().length() == 11
                )
        );
    }

    @Test
    void deveTratarSegundoTelefoneVazio() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("name", "UBS TESTE");
        properties.put("CNES", "123");
        properties.put("Telefones", "51999999999 / ");

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Point");

        ArrayNode coordinates = objectMapper.createArrayNode();
        coordinates.add(-51.0);
        coordinates.add(-30.0);

        geometry.set("coordinates", coordinates);

        feature.set("properties", properties);
        feature.set("geometry", geometry);

        features.add(feature);
        geojson.set("features", features);

        when(servicoRepository.findByCnes("123"))
                .thenReturn(Optional.empty());

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.of(new Servico()));

        service.importar(geojson);

        verify(servicoRepository).save(
                argThat(u ->
                        "51999999999".equals(u.getTelefone())
                                && u.getTelefone2() == null
                )
        );
    }

    @Test
    void deveImportarTerritorioGeometryCollectionComMultiPolygon() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("name", "Território Teste");
        properties.put("CNES", "123");
        properties.put("Distrito_S", "Centro");

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "GeometryCollection");

        ArrayNode geometries = objectMapper.createArrayNode();

        ObjectNode multiPolygon = objectMapper.createObjectNode();
        multiPolygon.put("type", "MultiPolygon");

        ArrayNode coordinates = objectMapper.createArrayNode();

        ArrayNode polygon1 = objectMapper.createArrayNode();
        polygon1.add(objectMapper.createArrayNode());

        ArrayNode polygon2 = objectMapper.createArrayNode();
        polygon2.add(objectMapper.createArrayNode());

        coordinates.add(polygon1);
        coordinates.add(polygon2);

        multiPolygon.set("coordinates", coordinates);

        geometries.add(multiPolygon);

        geometry.set("geometries", geometries);

        feature.set("properties", properties);
        feature.set("geometry", geometry);

        features.add(feature);
        geojson.set("features", features);

        when(servicoRepository.findByCnes("2264706"))
                .thenReturn(Optional.of(new Servico()));

        when(servicoRepository.findByCnes("123"))
                .thenReturn(Optional.of(new Servico()));

        service.importar(geojson);

        verify(territorioUbsRepository)
                .save(any(TerritorioUbs.class));
    }

    @Test
    void deveRetornarNullQuandoTelefoneForNull() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod("normalizarTelefone", String.class);

        method.setAccessible(true);

        String resultado =
                (String) method.invoke(service, new Object[]{null});

        assertNull(resultado);
    }
}