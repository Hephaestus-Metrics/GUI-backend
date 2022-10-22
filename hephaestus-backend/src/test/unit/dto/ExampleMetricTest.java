package dto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExampleMetricTest {
    private static final String NAME = "exampleName";
    private static final String KEY = "exampleKey";
    private static final String VALUE = "exampleValue";

    @Test
    void noAttributesSetTest() {
        //given
        ExampleMetric exampleMetric = new ExampleMetric();

        //when
        String metricName = exampleMetric.getName();
        Map<String, String> metricLabels = exampleMetric.getLabels();

        //then
        assertNull(metricName);
        assertNull(metricLabels);
    }

    @Test
    void onlyNameSetTest() {
        //given
        ExampleMetric exampleMetric = new ExampleMetric();
        exampleMetric.setName(NAME);

        //when
        String metricName = exampleMetric.getName();
        Map<String, String> metricLabels = exampleMetric.getLabels();

        //then
        assertEquals(NAME, metricName);
        assertNull(metricLabels);
    }

    @Test
    void onlyLabelsSetTest() {
        //given
        ExampleMetric exampleMetric = new ExampleMetric();
        exampleMetric.setLabels(new HashMap<>() {{
            put(KEY, VALUE);
        }});

        //when
        String metricName = exampleMetric.getName();
        Map<String, String> metricLabels = exampleMetric.getLabels();

        //then
        assertNull(metricName);
        assertFalse(metricLabels.isEmpty());
        assertEquals(VALUE, metricLabels.get(KEY));
    }

    @Test
    void everyAttributeSet() {
        //given
        ExampleMetric exampleMetric = new ExampleMetric();
        exampleMetric.setName(NAME);
        exampleMetric.setLabels(new HashMap<>() {{
            put(KEY, VALUE);
        }});

        //when
        String metricName = exampleMetric.getName();
        Map<String, String> metricLabels = exampleMetric.getLabels();

        //then
        assertEquals(NAME, metricName);
        assertFalse(metricLabels.isEmpty());
        assertEquals(VALUE, metricLabels.get(KEY));
    }
}
