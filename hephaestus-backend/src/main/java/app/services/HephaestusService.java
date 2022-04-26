package app.services;

import dto.ExampleMetric;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import responses.metrics.ExampleMetricResponseEntity;
import responses.metrics.save.SaveMetricResponseEntity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HephaestusService {

    public ResponseEntity getMetrics() {
        //only for testing
        Map<String, String> map = new HashMap<>();
        map.put("ThirdLabel", "ThirdValue");
        map.put("Label2", "LabelValue2");
        ExampleMetric exampleMetric = new ExampleMetric("MetricName", map);
        Map<String, String> map2 = new HashMap<>();
        map2.put("ThirdLabel", "ThirdValue");
        ExampleMetric exampleMetric2 = new ExampleMetric("MetricName2", map2);
        Map<String, String> map3 = new HashMap<>();
        map3.put("Label2", "LabelValue2");
        ExampleMetric exampleMetric3 = new ExampleMetric("MetricName2", map3);
        List<ExampleMetric> list = new ArrayList<>();
        list.add(exampleMetric);
        list.add(exampleMetric2);
        list.add(exampleMetric3);
        ExampleMetricResponseEntity responseEntity = new ExampleMetricResponseEntity(HttpStatus.OK, list);
        return new ResponseEntity<Object>(responseEntity.toResponseMap(), responseEntity.getStatusCode());
    }

    public ResponseEntity saveChoosenMetrics(String[][] body) {
        //todo REFACTOR!!! KS
        StringBuilder stringBuilder = new StringBuilder();
        for(String[] metric: body) {
            for(String label: metric) {
                stringBuilder.append(label + "\t");
            }
            stringBuilder.append("\n");
        }
        String resultString = stringBuilder.toString();
        try {
            System.out.println(System.getProperty("user.dir"));
            FileWriter output = new FileWriter(System.getProperty("user.dir") + "/hephaestus-backend/src/main/resources/metrics/Metrics.txt");
            output.write(resultString);
            output.close();
        } catch (Exception e) {
            e.getStackTrace();
        }

        SaveMetricResponseEntity responseEntity = new SaveMetricResponseEntity(HttpStatus.OK, "Successfully saved");
        return new ResponseEntity<Object>(responseEntity.toResponseMap(), responseEntity.getStatusCode());
    }

}
