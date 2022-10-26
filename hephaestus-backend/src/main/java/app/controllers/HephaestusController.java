package app.controllers;

import app.model.SelectedFilters;
import app.model.SelectedQuery;
import app.services.PrometheusService;
import app.volume.VolumeManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import conf.Configuration;
import io.github.hephaestusmetrics.model.queryresults.RawQueryResult;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("hephaestus")
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
@Log4j2
public class HephaestusController {

    private final VolumeManager volumeManager;
    private final PrometheusService prometheusService;
    private List<SelectedQuery> selectedQueries;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HephaestusController(PrometheusService prometheusService, VolumeManager volumeManager) {
        this.prometheusService = prometheusService;
        this.volumeManager = volumeManager;
        this.selectedQueries = volumeManager.loadQueries();
    }

    @RequestMapping(value = "/metrics/save", method = RequestMethod.PUT)
    public ResponseEntity<Void> saveMetrics(@RequestBody String body) {
        log.info("Save metrics: " + body);
        // TODO still uses org.json
        JSONArray array = new JSONArray(body);
        selectedQueries = IntStream.range(0, array.length())
                .mapToObj(array::getJSONObject)
                .map(Object::toString)
                .map(string -> {
                    try {
                        // TODO only supports Selected Filters, not complex queries
                        return objectMapper.readValue(string, SelectedFilters.class);
                    } catch (JsonProcessingException e) {
                        // TODO
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        return this.volumeManager.saveQueries(selectedQueries);
    }

    @RequestMapping(value = "/metrics/selected", method = RequestMethod.GET)
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

    @RequestMapping(value = "/metrics/saved", method = RequestMethod.GET, produces = "application/json")
    public List<SelectedQuery> getSaved() {
        log.info("Returning {} saved metrics", this.selectedQueries.size());
        return this.selectedQueries;
    }

}


