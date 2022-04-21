package app.services;

import dto.ExampleMetric;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import responses.metrics.ExampleMetricResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Service
public class HephaestusService {

    public ResponseEntity getMetrics() {
        //only for testing
        Map<String, String> map = new HashMap<>();
        map.put("Label", "LabelValue");
        map.put("AnotherLabel", "AnotherValue");
        ExampleMetric exampleMetric = new ExampleMetric("MetricName", map);
        ExampleMetricResponseEntity responseEntity = new ExampleMetricResponseEntity(HttpStatus.OK, exampleMetric);
        return new ResponseEntity<Object>(responseEntity.toResponseMap(), responseEntity.getStatusCode());
    }

}
