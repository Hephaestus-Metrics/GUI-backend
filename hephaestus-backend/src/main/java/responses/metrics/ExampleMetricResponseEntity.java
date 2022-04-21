package responses.metrics;

import dto.ExampleMetric;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExampleMetricResponseEntity extends ResponseEntity {

    private ExampleMetric exampleMetric;
    private HttpStatus status;

    public ExampleMetricResponseEntity(HttpStatus status, ExampleMetric exampleMetric) {
        super(status);
        this.status = status;
        this.exampleMetric = exampleMetric;
    }

    public Map<String, Object> toResponseMap() {
        Map<String, Object> response = new HashMap<>();
        response.put("Status", status.value());
        response.put("Data", toResponseMap(exampleMetric));
        return response;
    }

    private Map<String, Object> toResponseMap(ExampleMetric exampleMetric) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Name", exampleMetric.getName());
        parameters.put("Labels", toResponseMap(exampleMetric.getLabels()));
        return parameters;
    }

    private Map<String, Object> toResponseMap(Map<String, String> labels) {
        Map<String, Object> parameters = new HashMap<>();
        Iterator<Map.Entry<String, String>> iterator = labels.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            parameters.put(entry.getKey(), entry.getValue());
        }
        return parameters;
    }
}
