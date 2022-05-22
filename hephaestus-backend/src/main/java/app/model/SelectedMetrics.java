package app.model;

import java.util.List;

public class SelectedMetrics {

    //TODO in the future we may add fields to this class if we add new formats for storing metrics (e.g. named queries)
    private final List<String> simpleMetrics; // the strings are JSON strings returned by Prometheus as a result of querying

    public SelectedMetrics(List<String> simpleMetrics) {
        this.simpleMetrics = simpleMetrics;
    }

    public List<String> getSimpleMetrics() {
        return simpleMetrics;
    }


}
