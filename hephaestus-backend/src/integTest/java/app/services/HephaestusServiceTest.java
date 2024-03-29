package app.services;

import app.model.SelectedCustomQuery;
import app.model.SelectedQuery;
import app.volume.VolumeManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hephaestusmetrics.model.queryresults.RawQueryResult;
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

    private static final String TAG = "TAG";
    private static final String QUERY_STRING = "QUERY_STRING";


    @BeforeEach
    void cleanUpBefore() {
        cleanUp();
    }

    @AfterEach
    void cleanUpAfter() {
        cleanUp();
    }

    @Test
    void saveSimpleQueriesTest() {
        //given
        HephaestusService hephaestusService = new HephaestusService(new VolumeManager(SAVED_PATH, CONFIG_PATH), new PrometheusService(new RestTemplateBuilder(), PROMETHEUS_ADDRESS), new ObjectMapper());
        String simpleQueriesJson = "[{\"filters\":{\"" + KEY + "\":\"" + VALUE + "\"}}]";

        //when
        ResponseEntity<?> responseEntity = hephaestusService.saveSimpleQueries(simpleQueriesJson);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void saveCustomQueriesTest() {
        //given
        HephaestusService hephaestusService = new HephaestusService(new VolumeManager(SAVED_PATH, CONFIG_PATH), new PrometheusService(new RestTemplateBuilder(), PROMETHEUS_ADDRESS), new ObjectMapper());
        String customQueryJson = "[{\"tag\":\"" + TAG + "\",\"queryString\":\"" + QUERY_STRING + "\"}]";

        //when
        ResponseEntity<?> responseEntity = hephaestusService.saveSimpleQueries(customQueryJson);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void shouldGetEmptySelectedMetrics() {
        //given
        HephaestusService hephaestusService = new HephaestusService(new VolumeManager(SAVED_PATH, CONFIG_PATH), new PrometheusService(new RestTemplateBuilder(), PROMETHEUS_ADDRESS), new ObjectMapper());

        //when
        List<RawQueryResult> selected = hephaestusService.getSelected();

        //then
        assertTrue(selected.isEmpty());
    }

    @Test
    void shouldGetSelectedMetrics() {
        //given
        String customQueryJson = "[{\"tag\":\"" + TAG + "\",\"queryString\":\"" + QUERY_STRING + "\"}]";
        SelectedCustomQuery selectedCustomQuery = new SelectedCustomQuery(TAG, QUERY_STRING);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(restTemplate.getForObject(PROMETHEUS_ADDRESS + QUERY_SUFFIX, String.class, selectedCustomQuery.getQueryString())).thenReturn(VALUE);

        HephaestusService hephaestusService = new HephaestusService(new VolumeManager(SAVED_PATH, CONFIG_PATH), new PrometheusService(restTemplateBuilder, PROMETHEUS_ADDRESS), new ObjectMapper());
        hephaestusService.saveCustomQueries(customQueryJson);

        //when
        List<RawQueryResult> selected = hephaestusService.getSelected();

        //then
        assertEquals(VALUE, selected.get(0).getMetric());
    }

    @Test
    void shouldGetSavedSimpleQuery() {
        //given
        String simpleQueriesJson = "[{\"filters\":{\"" + KEY + "\":\"" + VALUE + "\"}}]";

        HephaestusService hephaestusService = new HephaestusService(new VolumeManager(SAVED_PATH, CONFIG_PATH), new PrometheusService(new RestTemplateBuilder(), PROMETHEUS_ADDRESS), new ObjectMapper());
        hephaestusService.saveSimpleQueries(simpleQueriesJson);

        //when
        List<SelectedQuery> saved = hephaestusService.getSaved(false);

        //then
        assertEquals("__#AUTOGENERATED__{" + KEY + "=" + VALUE + "}", saved.get(0).getTag());
        assertEquals("{" + KEY + "=\"" + VALUE + "\"}", saved.get(0).getQueryString());
    }

    @Test
    void shouldGetSavedCustomQuery() {
        //given
        String customQueryJson = "[{\"tag\":\"" + TAG + "\",\"queryString\":\"" + QUERY_STRING + "\"}]";

        HephaestusService hephaestusService = new HephaestusService(new VolumeManager(SAVED_PATH, CONFIG_PATH), new PrometheusService(new RestTemplateBuilder(), PROMETHEUS_ADDRESS), new ObjectMapper());
        hephaestusService.saveCustomQueries(customQueryJson);

        //when
        List<SelectedQuery> saved = hephaestusService.getSaved(true);

        //then
        assertEquals(TAG, saved.get(0).getTag());
        assertEquals(QUERY_STRING, saved.get(0).getQueryString());
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
