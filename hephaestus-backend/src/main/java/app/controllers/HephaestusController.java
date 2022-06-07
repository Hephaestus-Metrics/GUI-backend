package app.controllers;

import app.model.Filters;
import app.model.SelectedMetrics;
import app.services.HephaestusService;
import app.services.PrometheusService;
import app.services.QueryBuilderService;
import app.volume.VolumeManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import conf.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("hephaestus")
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
public class HephaestusController {

    private final HephaestusService hephaestusService;
    private final QueryBuilderService queryBuilderService;
    private final PrometheusService prometheusService;
    private List<Filters> selectedQueries;

    public HephaestusController(HephaestusService hephaestusService, QueryBuilderService queryBuilderService, PrometheusService prometheusService) {
        this.hephaestusService = hephaestusService;
        this.queryBuilderService = queryBuilderService;
        this.prometheusService = prometheusService;
        // read metrics from volume
        this.selectedQueries = VolumeManager.loadMetrics(false);
        if (this.selectedQueries.size() == 0){
            this.selectedQueries = VolumeManager.loadMetrics(true);
        }
    }

    @RequestMapping(value = "/metrics/save", method = RequestMethod.PUT)
    public ResponseEntity saveMetrics(@RequestBody Filters[] body) {
        selectedQueries = Arrays.stream(body).collect(Collectors.toList());
        return this.hephaestusService.saveChosenMetrics(body);
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


