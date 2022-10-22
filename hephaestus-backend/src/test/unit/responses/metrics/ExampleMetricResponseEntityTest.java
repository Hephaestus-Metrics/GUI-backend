package responses.metrics;

import dto.ExampleMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ExampleMetricResponseEntityTest {
    private final static HttpStatus HTTP_STATUS = HttpStatus.OK;
    private List<ExampleMetric> exampleMetricList;

    private final static String NAME1 = "Name1";
    private final static String NAME2 = "Name2";
    private final static Map<String, String> LABELS = new HashMap<>() {{
        put("KEY", "VALUE");
    }};

    @BeforeEach
    void init() {
        ExampleMetric exampleMetric1 = new ExampleMetric();
        exampleMetric1.setName(NAME1);
        exampleMetric1.setLabels(LABELS);

        ExampleMetric exampleMetric2 = new ExampleMetric();
        exampleMetric2.setName(NAME2);
        exampleMetric2.setLabels(LABELS);

        exampleMetricList = new ArrayList<>() {{
            add(exampleMetric1);
            add(exampleMetric2);
        }};
    }

    @Test
    void responseMapTest() {
        //given
        ExampleMetricResponseEntity exampleMetricResponseEntity = new ExampleMetricResponseEntity(HTTP_STATUS, exampleMetricList);

        //when
        Map<String, Object> responseMap = exampleMetricResponseEntity.toResponseMap();

        //then
        assertFalse(responseMap.isEmpty());
        assertEquals(HttpStatus.OK.value(), responseMap.get("Status"));
        assertEquals(exampleMetricList, responseMap.get("Data"));
    }
}
