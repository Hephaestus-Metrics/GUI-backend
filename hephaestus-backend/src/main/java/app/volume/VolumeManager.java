package app.volume;

import app.exceptions.VolumeManagerException;
import app.model.Filters;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import responses.metrics.save.SaveMetricResponseEntity;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class VolumeManager {

    private final String savedPath;

    private final String configPath;

    public VolumeManager(@Value("${config.path}") String configPath, @Value("${saved.path}") String savedPath) {
        this.configPath = configPath;
        this.savedPath = savedPath;
    }

    public ResponseEntity<?> saveMetrics(Filters[] body){
        log.info("Attempting to save {} metrics", body.length);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"chosenMetrics\":[");
        if (body.length > 0) {
            for (Filters metric : body) {
                stringBuilder.append(metric.toJSONString()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }
        stringBuilder.append("]}");
        String resultString = stringBuilder.toString();
        try {
            File saveFile = new File(savedPath);
            saveFile.getParentFile().mkdirs();
            FileWriter output = new FileWriter(saveFile);
            output.write(resultString);
            output.close();
        } catch (Exception e) {
            throw new VolumeManagerException("Unexpected exception occurred while saving metrics: " + e.getMessage());
        }
        log.info("Metrics saved successfully");
        SaveMetricResponseEntity responseEntity = new SaveMetricResponseEntity(HttpStatus.OK, "Successfully saved");
        return new ResponseEntity<Object>(responseEntity.toResponseMap(), responseEntity.getStatusCode());
    }

    /**
     * Attempts to load metrics from volume
     * @return metrics from volume or empty list
     */

    public List<Filters> loadMetrics() {
        List<Filters> selectedQueries;
        selectedQueries = loadMetrics(false);
        if (selectedQueries != null){
            selectedQueries = loadMetrics(true);
        }
        if (selectedQueries == null){
            selectedQueries = new ArrayList<>();
        }
        log.info("Loaded total of {} metrics at start", selectedQueries.size());
        return selectedQueries;
    }

    private List<Filters> loadMetrics(boolean fromConfigMap){
        String volumePath;
        volumePath = fromConfigMap ? configPath : savedPath;
        List<Filters> selectedQueries = new ArrayList<>();
        try {
            String jsonString = Files.readString(Paths.get(volumePath), StandardCharsets.US_ASCII);
            JSONArray jsonArr = new JSONObject(jsonString).getJSONArray("chosenMetrics");
            ObjectMapper mapper = new ObjectMapper();
            for (int i = 0; i < jsonArr.length(); i++) {
                String filtersStr = jsonArr.get(i).toString();
                Filters filters = mapper.readValue(filtersStr, Filters.class);
                selectedQueries.add(filters);
            }
        } catch (Exception e) {
            String source = fromConfigMap ? "config map" : "save file";
            log.warn(
                    "Attempted to load metrics from {} but failed. Resource path was {}",
                    source,
                    e.getMessage());
            return null;
        }
        return selectedQueries;
    }

}
