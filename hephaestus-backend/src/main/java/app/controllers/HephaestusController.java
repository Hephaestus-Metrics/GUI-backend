package app.controllers;

import app.model.Filters;
import app.model.SelectedMetrics;
import app.services.PrometheusService;
import app.services.QueryBuilderService;
import conf.Configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hephaestus")
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
@Log4j2
public class HephaestusController {

    private final QueryBuilderService queryBuilderService;
    private final PrometheusService prometheusService;

    public HephaestusController(QueryBuilderService queryBuilderService, PrometheusService prometheusService) {
        this.queryBuilderService = queryBuilderService;
        this.prometheusService = prometheusService;
    }

    @RequestMapping(value = "/metrics/save", method = RequestMethod.PUT)
    public ResponseEntity saveMetrics(@RequestBody Filters[] body) {
        return this.prometheusService.saveMetrics(body);
    }

    @RequestMapping(value = "/metrics/selected", method = RequestMethod.GET)
    public SelectedMetrics getSelected() {
        return this.prometheusService.getSelectedMetrics();
    }

    @RequestMapping(value = "/metrics/saved", method = RequestMethod.GET, produces = "application/json")
    public List<Filters> getSaved() {
        return this.prometheusService.getSavedMetrics();
    }
}


