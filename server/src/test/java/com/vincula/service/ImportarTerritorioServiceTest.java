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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Method;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportarTerritorioServiceTest {

    @Mock
    private TerritorioUbsRepository territorioUbsRepository;

    @Mock
    private ServicoRepository servicoRepository;

    private ObjectMapper objectMapper;

    @InjectMocks
    private ImportarTerritorioService service;

    @BeforeEach
    void setup() {

        objectMapper = new ObjectMapper();

        service = new ImportarTerritorioService(
                territorioUbsRepository,
                servicoRepository,
                objectMapper
        );
    }

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

        List<TerritorioUbsDTO> resultado =
                service.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals(
                "Território A",
                resultado.get(0).getNome()
        );
        assertEquals(
                "123",
                resultado.get(0).getCnes()
        );
    }

    @Test
    void deveConverterGeomParaGeoJson() {

        Servico servico = new Servico();

        servico.setTelefone("51999999999");
        servico.setTelefone2("51888888888");
        servico.setEndereco(new Endereco());

        TerritorioUbs territorio = new TerritorioUbs();

        territorio.setNome("Território A");
        territorio.setCnes("123");
        territorio.setDistrito("Norte");
        territorio.setServico(servico);

        GeometryFactory geometryFactory =
                new GeometryFactory();

        Point ponto =
                geometryFactory.createPoint(
                        new Coordinate(-51.0, -30.0)
                );

        territorio.setGeom(ponto);

        when(territorioUbsRepository.findAll())
                .thenReturn(List.of(territorio));

        List<TerritorioUbsDTO> resultado =
                service.listarTodos();

        assertEquals(1, resultado.size());

        String geom =
                resultado.get(0).getGeom();

        assertNotNull(geom);
        assertTrue(geom.contains("Point"));
        assertTrue(geom.contains("-51"));
        assertTrue(geom.contains("-30"));
    }

    @Test
    void deveLancarErroQuandoGeoJsonInvalido() {

        ObjectNode geojson =
                objectMapper.createObjectNode();

        RuntimeException ex =
                assertThrows(
                        RuntimeException.class,
                        () -> service.importar(geojson)
                );

        assertTrue(
                ex.getMessage()
                        .contains("GeoJSON inválido")
        );
    }

    @Test
    void deveRetornarNullQuandoTelefoneForNull()
            throws Exception {

        Method method =
                ImportarTerritorioService.class
                        .getDeclaredMethod(
                                "normalizarTelefone",
                                String.class
                        );

        method.setAccessible(true);

        String resultado =
                (String) method.invoke(
                        service,
                        new Object[]{null}
                );

        assertNull(resultado);
    }

    @Test
    void deveReutilizarServicoExistenteEncontradoPorCnes() {

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

        Servico servicoExistente = new Servico();
        servicoExistente.setId(10L);
        servicoExistente.setCnes("123");
        servicoExistente.setNome("Nome antigo");

        when(servicoRepository.findAllByCnesIn(anySet()))
                .thenReturn(List.of(servicoExistente));

        service.importar(geojson);

        verify(servicoRepository).saveAll(
                argThat((Iterable<Servico> servicos) -> {

                    for (Servico servico : servicos) {

                        if ("123".equals(servico.getCnes())) {

                            assertEquals(10L, servico.getId());
                            assertEquals("UBS TESTE", servico.getNome());

                            return true;
                        }
                    }

                    return false;
                })
        );
    }

    @Test
    void deveReutilizarTerritorioExistenteEncontradoPorCnes() {

        ObjectNode geojson =
                objectMapper.createObjectNode();

        ArrayNode features =
                objectMapper.createArrayNode();

        ObjectNode feature =
                objectMapper.createObjectNode();

        ObjectNode properties =
                objectMapper.createObjectNode();

        properties.put(
                "name",
                "Território Novo"
        );

        properties.put(
                "CNES",
                "123"
        );

        properties.put(
                "Distrito_S",
                "Centro"
        );

        ObjectNode geometry =
                objectMapper.createObjectNode();

        geometry.put(
                "type",
                "Polygon"
        );

        geometry.set(
                "coordinates",
                objectMapper.createArrayNode()
        );

        feature.set(
                "properties",
                properties
        );

        feature.set(
                "geometry",
                geometry
        );

        features.add(feature);

        geojson.set(
                "features",
                features
        );

        Servico servico =
                new Servico();

        servico.setId(1L);
        servico.setCnes("123");

        TerritorioUbs territorioExistente =
                new TerritorioUbs();

        territorioExistente.setId(10L);
        territorioExistente.setCnes("123");
        territorioExistente.setNome(
                "Nome antigo"
        );
        territorioExistente.setServico(
                servico
        );

        when(
                servicoRepository.findAllByCnesIn(
                        anySet()
                )
        ).thenReturn(
                List.of(servico)
        );

        when(
                territorioUbsRepository.findAllByCnesIn(
                        anySet()
                )
        ).thenReturn(
                List.of(territorioExistente)
        );

        service.importar(geojson);

        verify(territorioUbsRepository)
                .save(argThat(territorio ->
                        territorio.getId() != null
                                && territorio.getId().equals(10L)
                                && "123".equals(
                                territorio.getCnes()
                        )
                                && "Território Novo".equals(
                                territorio.getNome()
                        )
                                && "Centro".equals(
                                territorio.getDistrito()
                        )
                ));
    }

    @Test
    void devePreencherEnderecoQuandoTextoForNull() throws Exception {

        Endereco endereco = new Endereco();

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "preencherEndereco",
                        Endereco.class,
                        String.class,
                        Double.class,
                        Double.class
                );

        method.setAccessible(true);

        method.invoke(
                service,
                endereco,
                null,
                -30.0,
                -51.0
        );

        assertEquals("Porto Alegre", endereco.getCidade());
        assertEquals("RS", endereco.getEstado());
        assertEquals(-30.0, endereco.getLatitude());
        assertEquals(-51.0, endereco.getLongitude());

        assertEquals("Não informado", endereco.getRua());
        assertEquals("S/N", endereco.getNumero());
        assertEquals("Não informado", endereco.getBairro());
    }

    @Test
    void devePreencherEnderecoQuandoTextoForVazio() throws Exception {

        Endereco endereco = new Endereco();

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "preencherEndereco",
                        Endereco.class,
                        String.class,
                        Double.class,
                        Double.class
                );

        method.setAccessible(true);

        method.invoke(
                service,
                endereco,
                "   ",
                -30.0,
                -51.0
        );

        assertEquals("Não informado", endereco.getRua());
        assertEquals("S/N", endereco.getNumero());
        assertEquals("Não informado", endereco.getBairro());
    }

    @Test
    void deveExtrairNumeroDoEndereco() throws Exception {

        Endereco endereco = new Endereco();

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "preencherEndereco",
                        Endereco.class,
                        String.class,
                        Double.class,
                        Double.class
                );

        method.setAccessible(true);

        method.invoke(
                service,
                endereco,
                "Rua Flores 100",
                -30.0,
                -51.0
        );

        assertEquals("Rua Flores", endereco.getRua());
        assertEquals("100", endereco.getNumero());
        assertEquals("Não informado", endereco.getBairro());
    }

    @Test
    void deveManterNumeroSNNquandoEnderecoNaoPossuirNumero() throws Exception {

        Endereco endereco = new Endereco();

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "preencherEndereco",
                        Endereco.class,
                        String.class,
                        Double.class,
                        Double.class
                );

        method.setAccessible(true);

        method.invoke(
                service,
                endereco,
                "Rua Flores Bairro Centro",
                -30.0,
                -51.0
        );

        assertEquals("S/N", endereco.getNumero());
    }

    @Test
    void deveAceitarNumeroComLetra() throws Exception {

        Endereco endereco = new Endereco();

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "preencherEndereco",
                        Endereco.class,
                        String.class,
                        Double.class,
                        Double.class
                );

        method.setAccessible(true);

        method.invoke(
                service,
                endereco,
                "Rua Flores 100A Bairro Centro",
                -30.0,
                -51.0
        );

        assertEquals("100A", endereco.getNumero());
        assertEquals("Centro", endereco.getBairro());
    }

    @Test
    void deveRetornarTelefonesNulosQuandoTextoForNull() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "extrairTelefones",
                        String.class
                );

        method.setAccessible(true);

        String[] resultado =
                (String[]) method.invoke(service, new Object[]{null});

        assertNull(resultado[0]);
        assertNull(resultado[1]);
    }

    @Test
    void deveRetornarTelefonesNulosQuandoTextoForVazio() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "extrairTelefones",
                        String.class
                );

        method.setAccessible(true);

        String[] resultado =
                (String[]) method.invoke(service, "   ");

        assertNull(resultado[0]);
        assertNull(resultado[1]);
    }

    @Test
    void deveExtrairApenasUmTelefone() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "extrairTelefones",
                        String.class
                );

        method.setAccessible(true);

        String[] resultado =
                (String[]) method.invoke(
                        service,
                        "51999999999"
                );

        assertEquals("51999999999", resultado[0]);
        assertNull(resultado[1]);
    }

    @Test
    void deveExtrairDoisTelefones() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "extrairTelefones",
                        String.class
                );

        method.setAccessible(true);

        String[] resultado =
                (String[]) method.invoke(
                        service,
                        "51999999999 / 51888888888"
                );

        assertEquals("51999999999", resultado[0]);
        assertEquals("51888888888", resultado[1]);
    }

    @Test
    void deveRemoverWhatsAppDoTelefone() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "extrairTelefones",
                        String.class
                );

        method.setAccessible(true);

        String[] resultado =
                (String[]) method.invoke(
                        service,
                        "51999999999 (WhatsApp)"
                );

        assertEquals("51999999999", resultado[0]);
        assertNull(resultado[1]);
    }

    @Test
    void deveRetornarNullQuandoTelefoneForVazio() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "normalizarTelefone",
                        String.class
                );

        method.setAccessible(true);

        String resultado =
                (String) method.invoke(service, "   ");

        assertNull(resultado);
    }

    @Test
    void deveRemoverCaracteresDoTelefone() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "normalizarTelefone",
                        String.class
                );

        method.setAccessible(true);

        String resultado =
                (String) method.invoke(
                        service,
                        "(51) 99999-1111"
                );

        assertEquals("51999991111", resultado);
    }

    @Test
    void deveRemoverCodigoPais55() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "normalizarTelefone",
                        String.class
                );

        method.setAccessible(true);

        String resultado =
                (String) method.invoke(
                        service,
                        "+55 (51) 99999-1111"
                );

        assertEquals("51999991111", resultado);
    }

    @Test
    void deveLimitarTelefoneParaOnzeDigitos() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "normalizarTelefone",
                        String.class
                );

        method.setAccessible(true);

        String resultado =
                (String) method.invoke(
                        service,
                        "519999999999999"
                );

        assertEquals(11, resultado.length());
        assertEquals("51999999999", resultado);
    }

    @Test
    void deveAdicionarDdd51() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod(
                        "normalizarTelefone",
                        String.class
                );

        method.setAccessible(true);

        String resultado =
                (String) method.invoke(
                        service,
                        "9999911111"
                );

        assertEquals("51999991111", resultado);
    }

    @Test
    void deveCriarNovoTerritorioQuandoNaoExistir() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("name", "Território Novo");
        properties.put("CNES", "123");
        properties.put("Distrito_S", "Centro");

        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", "Polygon");

        ArrayNode coordinates = objectMapper.createArrayNode();
        geometry.set("coordinates", coordinates);

        feature.set("properties", properties);
        feature.set("geometry", geometry);

        features.add(feature);
        geojson.set("features", features);

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setCnes("123");

        when(servicoRepository.findAllByCnesIn(anySet()))
                .thenReturn(List.of(servico));

        when(territorioUbsRepository.findAllByCnesIn(anySet()))
                .thenReturn(List.of());

        service.importar(geojson);

        verify(territorioUbsRepository).save(
                argThat(territorio ->
                        territorio.getId() == null
                                && "123".equals(territorio.getCnes())
                                && "Território Novo".equals(territorio.getNome())
                                && "Centro".equals(territorio.getDistrito())
                                && territorio.getServico() == servico
                )
        );

        verify(territorioUbsRepository).atualizarGeom(
                eq("123"),
                anyString()
        );
    }

    @Test
    void deveRetornarNullQuandoValorForNull() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod("limparDecimal", String.class);

        method.setAccessible(true);

        String resultado =
                (String) method.invoke(service, new Object[]{null});

        assertNull(resultado);
    }

    @Test
    void deveRemoverEspacosDoCnes() throws Exception {

        Method method = ImportarTerritorioService.class
                .getDeclaredMethod("limparDecimal", String.class);

        method.setAccessible(true);

        String resultado =
                (String) method.invoke(service, " 12345.0 ");

        assertEquals("12345", resultado);
    }

    @Test
    void deveCriarNovoServicoQuandoServicoNaoExistir() {

        ObjectNode geojson = objectMapper.createObjectNode();
        ArrayNode features = objectMapper.createArrayNode();

        ObjectNode feature = objectMapper.createObjectNode();

        ObjectNode properties = objectMapper.createObjectNode();
        properties.put("name", "UBS NOVA");
        properties.put("CNES", "999");
        properties.put("Endereço", "Rua Flores 100 Bairro Centro");

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

        when(servicoRepository.findAllByCnesIn(anySet()))
                .thenReturn(List.of());

        service.importar(geojson);

        verify(servicoRepository).saveAll(
                argThat(lista -> {

                    for (Servico servico : lista) {

                        if ("999".equals(servico.getCnes())) {

                            assertNull(servico.getId());
                            assertEquals("UBS NOVA", servico.getNome());

                            return true;
                        }
                    }

                    return false;
                })
        );
    }
}