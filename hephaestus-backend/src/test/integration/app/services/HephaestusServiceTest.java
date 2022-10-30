package app.services;

import app.model.Filters;
import app.model.SelectedMetrics;
import app.volume.VolumeManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class HephaestusServiceTest {
    @Value("${prometheus.address}")
    private String PROMETHEUS_ADDRESS;
    @Value("${config.path}")
    private String CONFIG_PATH;
    @Value("${saved.path}")
    private String SAVED_PATH;
    private final String QUERY_SUFFIX = "/api/v1/query?query={my_query}";

    @Mock
    RestTemplate restTemplate;

    @Mock
    RestTemplateBuilder restTemplateBuilder;

    private static final String KEY = "FILTER";
    private static final String VALUE = "VALUE";


    @BeforeEach
    void cleanUpBefore() {
        cleanUp();
    }

    @AfterEach
    void cleanUpAfter() {
        cleanUp();
    }

    @Test
    void saveMetricsTest() {
        //given
        HephaestusService hephaestusService = new HephaestusService(new VolumeManager(CONFIG_PATH, SAVED_PATH), new QueryBuilderService(), new PrometheusService(new RestTemplateBuilder(), PROMETHEUS_ADDRESS), new RestTemplateBuilder());

        //when
        ResponseEntity<?> responseEntity = hephaestusService.saveMetrics(new Filters[0]);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void shouldGetEmptySelectedMetrics() {
        //given
        HephaestusService hephaestusService = new HephaestusService(new VolumeManager(CONFIG_PATH, SAVED_PATH), new QueryBuilderService(), new PrometheusService(new RestTemplateBuilder(), PROMETHEUS_ADDRESS), new RestTemplateBuilder());

        //when
        SelectedMetrics selectedMetrics = hephaestusService.getSelectedMetrics();

        //then
        assertTrue(selectedMetrics.getSimpleMetrics().isEmpty());
    }

    @Test
    void shouldGetSelectedMetrics() {
        //given
        VolumeManager volumeManager = new VolumeManager(CONFIG_PATH, SAVED_PATH);
        Filters filters = new Filters();
        filters.setValues(new HashMap<>() {{
            put(KEY, VALUE);
        }});
        volumeManager.saveMetrics(new Filters[]{filters});

        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(restTemplate.getForObject(PROMETHEUS_ADDRESS + QUERY_SUFFIX, String.class, new QueryBuilderService().filtersToQuery(filters))).thenReturn(VALUE);

        HephaestusService hephaestusService = new HephaestusService(volumeManager, new QueryBuilderService(), new PrometheusService(new RestTemplateBuilder(), PROMETHEUS_ADDRESS), restTemplateBuilder);

        //when
        SelectedMetrics selectedMetrics = hephaestusService.getSelectedMetrics();

        //then
        assertEquals(VALUE, selectedMetrics.getSimpleMetrics().get(0));
    }

    @Test
    void shouldGetSavedMetrics() {
        //given
        VolumeManager volumeManager = new VolumeManager(CONFIG_PATH, SAVED_PATH);
        Filters filters = new Filters();
        filters.setValues(new HashMap<>() {{
            put(KEY, VALUE);
        }});
        volumeManager.saveMetrics(new Filters[]{filters});
        HephaestusService hephaestusService = new HephaestusService(volumeManager, new QueryBuilderService(), new PrometheusService(new RestTemplateBuilder(), PROMETHEUS_ADDRESS), new RestTemplateBuilder());

        //when
        List<Filters> savedMetrics = hephaestusService.getSavedMetrics();

        //then
        assertEquals(VALUE, savedMetrics.get(0).getValues().get(KEY));
    }

    private static void deleteFileFromPathIfExists(String path) {
        File savedFile = new File(path);
        if (savedFile.isFile()) {
            savedFile.delete();
        }
    }

    private void cleanUp() {
        deleteFileFromPathIfExists(SAVED_PATH);
        deleteFileFromPathIfExists(CONFIG_PATH);
    }
}
