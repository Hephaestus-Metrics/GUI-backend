package app.volume;

import app.model.Filters;
import com.fasterxml.jackson.databind.ObjectMapper;
import conf.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VolumeManager {


    private static final String savedPath = "/../metrics/savedMetrics/metrics.json";
    private final static String configPath = "/../metrics/configMetrics/metrics.json";

    public static void saveMetrics(Filters[] body){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"chosenMetrics\":[");
        for(Filters metric: body) {
            stringBuilder.append(metric.toJSONString()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        stringBuilder.append("]}");
        String resultString = stringBuilder.toString();
        try {
            File saveFile = new File(savedPath);
            saveFile.getParentFile().mkdirs();
            FileWriter output = new FileWriter(saveFile);
            output.write(resultString);
            output.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /**
     * Loads metrics from volume
     * @param fromConfigMap - whether to load from config map instead of default volume
     * @return metrics from volume, empty list if exception occured
     */
    public static List<Filters> loadMetrics(boolean fromConfigMap){
        String volumePath;
        if (fromConfigMap) {
            volumePath = configPath;
        }
        else{
            volumePath = savedPath;
        }
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
            System.out.println("Cannot read volume: " + volumePath);
        }
        return selectedQueries;
    }

}
