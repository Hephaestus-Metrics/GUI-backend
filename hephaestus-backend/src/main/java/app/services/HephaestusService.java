package app.services;

import dto.ExampleMetric;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import responses.metrics.ExampleMetricResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HephaestusService {

    public ResponseEntity getMetrics() {
        //only for testing
        Map<String, String> map = new HashMap<>();
        map.put("Label", "LabelValue");
        map.put("AnotherLabel", "AnotherValue");
        ExampleMetric exampleMetric = new ExampleMetric("MetricName", map);
        Map<String, String> map2 = new HashMap<>();
        map2.put("Label2", "LabelValue2");
        map2.put("ThirdLabel", "ThirdValue");
        ExampleMetric exampleMetric2 = new ExampleMetric("MetricName2", map2);
        List<ExampleMetric> list = new ArrayList<>();
        list.add(exampleMetric);
        list.add(exampleMetric2);
        ExampleMetricResponseEntity responseEntity = new ExampleMetricResponseEntity(HttpStatus.OK, list);
        return new ResponseEntity<Object>(responseEntity.toResponseMap(), responseEntity.getStatusCode());
    }

}