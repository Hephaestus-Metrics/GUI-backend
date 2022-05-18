package app.controllers;

import app.model.Filters;
import conf.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.HephaestusService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("hephaestus")
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
public class HephaestusController {
    private final HephaestusService hephaestusService;
    private List<Filters> selectedQueries;

    public HephaestusController(HephaestusService hephaestusService) {
        this.hephaestusService = hephaestusService;
    }

    @RequestMapping(value = "/metrics/save", method = RequestMethod.PUT)
    public ResponseEntity saveMetrics(@RequestBody Filters[] body) {
        //todo have to refactor!!! KS
        selectedQueries = Arrays.stream(body).collect(Collectors.toList());
        return this.hephaestusService.saveChoosenMetrics(body);
    }

    @RequestMapping(value = "/metrics/selected", method = RequestMethod.GET)
    public String getSelected(){
        return null;
    }

}
