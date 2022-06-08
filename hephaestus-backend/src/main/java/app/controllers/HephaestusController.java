package app.controllers;

import app.model.Filters;
import app.model.SelectedMetrics;
import app.services.HephaestusService;
import app.services.PrometheusService;
import app.services.QueryBuilderService;
import app.volume.VolumeManager;
import conf.Configuration;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("hephaestus")
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
public class HephaestusController {

    //private final HephaestusService hephaestusService;
    private final VolumeManager volumeManager;
    private final QueryBuilderService queryBuilderService;
    private final PrometheusService prometheusService;
    private List<Filters> selectedQueries;

    public HephaestusController(QueryBuilderService queryBuilderService, PrometheusService prometheusService, VolumeManager volumeManager) {
        //this.hephaestusService = hephaestusService;
        this.queryBuilderService = queryBuilderService;
        this.prometheusService = prometheusService;
        this.volumeManager = volumeManager;
        // read metrics from volume
        this.selectedQueries = volumeManager.loadMetrics(false);
        if (this.selectedQueries == null){
            // read from config map
            this.selectedQueries = volumeManager.loadMetrics(true);
        }
        // all reads failed
        if (this.selectedQueries == null){
            this.selectedQueries = new ArrayList<>();
        }
    }

    @RequestMapping(value = "/metrics/save", method = RequestMethod.PUT)
    public ResponseEntity saveMetrics(@RequestBody Filters[] body) {
        selectedQueries = Arrays.stream(body).collect(Collectors.toList());
        return this.volumeManager.saveMetrics(body);
    }

    @RequestMapping(value = "/metrics/selected", method = RequestMethod.GET)
    public SelectedMetrics getSelected() {
        if (selectedQueries != null) {
            List<String> queryResults = selectedQueries.parallelStream()
                    .map(queryBuilderService::filtersToQuery)
                    .map(prometheusService::query)
                    .collect(Collectors.toList());
            return new SelectedMetrics(queryResults);
        } else {
            return new SelectedMetrics(new ArrayList<>());
        }
    }

    @RequestMapping(value = "/metrics/saved", method = RequestMethod.GET, produces = "application/json")
    public List<Filters> getSaved() {
        return this.selectedQueries;
    }
}


