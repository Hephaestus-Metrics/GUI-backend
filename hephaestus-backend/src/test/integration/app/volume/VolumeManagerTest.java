package app.volume;

import app.model.Filters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VolumeManagerTest {
    private static final String CONFIG_PATH = "./test_config";
    private static final String SAVED_PATH = "./test_saved";

    private static final String KEY_1 = "FILTER_1";
    private static final String VALUE_1 = "VALUE_1";

    private static final String KEY_2 = "FILTER_2";
    private static final String VALUE_2 = "VALUE_2";

    private static final HttpStatus EXPECTED_STATUS = HttpStatus.OK;
    private Filters[] filters;

    @BeforeAll
    static void cleanUpBefore() {
        cleanUp();
    }

    @AfterEach
    void cleanUpAfter() {
        cleanUp();
    }

    @BeforeEach
    void initFilters() {
        Filters filters1 = new Filters();
        filters1.setValues(new HashMap<>() {{
            put(KEY_1, VALUE_1);
        }});
        Filters filters2 = new Filters();
        filters2.setValues(new HashMap<>() {{
            put(KEY_2, VALUE_2);
        }});
        filters = new Filters[]{filters1, filters2};
    }

    @Test
    void testIfFileExistsAfterSave() {
        //given
        VolumeManager volumeManager = new VolumeManager(CONFIG_PATH, SAVED_PATH);

        //when
        ResponseEntity<?> responseEntity = volumeManager.saveMetrics(filters);
        File savedFile = new File(SAVED_PATH);

        //then
        assertEquals(EXPECTED_STATUS, responseEntity.getStatusCode());
        assertTrue(savedFile.isFile());
    }

    @Test
    void testIfFileContainsMetricsAfterSave() throws IOException {
        //given
        VolumeManager volumeManager = new VolumeManager(CONFIG_PATH, SAVED_PATH);

        //when
        volumeManager.saveMetrics(filters);
        String fileContent = Files.readAllLines(Paths.get(SAVED_PATH)).get(0);

        //then
        assertTrue(fileContent.contains("\"" + KEY_1 + "\":\"" + VALUE_1 + "\""));
        assertTrue(fileContent.contains("\"" + KEY_2 + "\":\"" + VALUE_2 + "\""));
    }

    @Test
    void loadMetricsFromSavedTest() {
        //given
        VolumeManager volumeManager = new VolumeManager(CONFIG_PATH, SAVED_PATH);

        //when
        volumeManager.saveMetrics(filters);
        List<Filters> metrics = volumeManager.loadMetrics();

        //then
        assertEquals(VALUE_1, metrics.get(0).getValues().get(KEY_1));
        assertEquals(VALUE_2, metrics.get(1).getValues().get(KEY_2));
    }

    @Test
    void loadMetricsFromConfigTest() throws IOException {
        //given
        VolumeManager volumeManager = new VolumeManager(CONFIG_PATH, SAVED_PATH);
        File configMap = new File(CONFIG_PATH);
        FileWriter output = new FileWriter(configMap);
        output.write("{\"chosenMetrics\":[{\"values\":{\"" + KEY_1 + "\":\"" + VALUE_1 + "\"},\"isQuery\":false}" +
                ",{\"values\":{\"" + KEY_2 + "\":\"" + VALUE_2 + "\"},\"isQuery\":false}]}");
        output.close();

        //when
        List<Filters> metrics = volumeManager.loadMetrics();

        //then
        assertEquals(VALUE_1, metrics.get(0).getValues().get(KEY_1));
        assertEquals(VALUE_2, metrics.get(1).getValues().get(KEY_2));
    }

    private static void deleteFileFromPathIfExists(String path) {
        File savedFile = new File(path);
        if (savedFile.isFile()) {
            savedFile.delete();
        }
    }

    private static void cleanUp() {
        deleteFileFromPathIfExists(SAVED_PATH);
        deleteFileFromPathIfExists(CONFIG_PATH);
    }
}
