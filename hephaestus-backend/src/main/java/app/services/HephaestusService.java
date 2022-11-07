package app.services;

import app.model.SelectedQuery;
import app.volume.VolumeManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hephaestusmetrics.model.queryresults.RawQueryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Log4j2
@RequiredArgsConstructor
public class HephaestusService {

    private final VolumeManager volumeManager;
    private final PrometheusService prometheusService;
    private final ObjectMapper objectMapper;

    private List<SelectedQuery> selectedQueries;

    public ResponseEntity<Void> saveMetrics(@RequestBody String body) {
        log.info("Save metrics: " + body);
        // TODO still uses org.json
        JSONArray array = new JSONArray(body);
        selectedQueries = IntStream.range(0, array.length())
                .mapToObj(array::getJSONObject)
                .map(Object::toString)
                .map(string -> {
                    try {
                        return objectMapper.readValue(string, SelectedQuery.class);
                    } catch (JsonProcessingException e) {
                        // TODO ugly exception in functional pipeline
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        return this.volumeManager.saveQueries(selectedQueries);
    }

    public List<RawQueryResult> getSelected() {
        if (selectedQueries != null) {
            List<RawQueryResult> queryResults = selectedQueries.parallelStream()
                    .map(prometheusService::query)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            log.info("Returning {} selected metrics", queryResults.size());
            return queryResults;
        } else {
            log.info("No metrics selected - returning an empty list");
            return Collections.emptyList();
        }
    }

    public List<SelectedQuery> getSaved() {
        log.info("Returning {} saved metrics", this.selectedQueries.size());
        return this.selectedQueries;
    }

}
