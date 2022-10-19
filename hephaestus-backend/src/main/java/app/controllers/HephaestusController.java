package app.controllers;

import app.model.RawMetric;
import app.model.SelectedMetrics;
import app.model.SelectedQuery;
import app.services.PrometheusService;
import app.services.QueryBuilderService;
import app.volume.VolumeManager;
import conf.Configuration;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("hephaestus")
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
@Log4j2
public class HephaestusController {

    private final VolumeManager volumeManager;
    private final PrometheusService prometheusService;
    private List<SelectedQuery> selectedQueries;

    public HephaestusController(PrometheusService prometheusService, VolumeManager volumeManager) {
        this.prometheusService = prometheusService;
        this.volumeManager = volumeManager;
        this.selectedQueries = volumeManager.loadQueries();
    }

    @RequestMapping(value = "/metrics/save", method = RequestMethod.PUT)
    public ResponseEntity<Void> saveMetrics(@RequestBody SelectedQuery[] body) {
        selectedQueries = Arrays.stream(body).collect(Collectors.toList());
        return this.volumeManager.saveQueries(body);
    }

    @RequestMapping(value = "/metrics/selected", method = RequestMethod.GET)
    public SelectedMetrics getSelected() {
        if (selectedQueries != null) {
            List<RawMetric> queryResults = selectedQueries.parallelStream()
                    .map(prometheusService::query)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            log.info("Returning {} selected metrics metrics", queryResults.size());
            return new SelectedMetrics(queryResults);
        } else {
            log.info("No metrics selected - returning an empty list");
            return new SelectedMetrics(Collections.emptyList());
        }
    }

    @RequestMapping(value = "/metrics/saved", method = RequestMethod.GET, produces = "application/json")
    public List<SelectedQuery> getSaved() {
        log.info("Returning {} saved metrics", this.selectedQueries.size());
        return this.selectedQueries;
    }
}


