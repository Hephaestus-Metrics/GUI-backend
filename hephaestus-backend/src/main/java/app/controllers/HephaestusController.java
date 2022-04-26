package app.controllers;

import conf.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.HephaestusService;

@RestController
@RequestMapping("hephaestus")
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
public class HephaestusController {
    private final HephaestusService hephaestusService;

    public HephaestusController(HephaestusService hephaestusService) {
        this.hephaestusService = hephaestusService;
    }

    @RequestMapping(value = "/metrics/save", method = RequestMethod.PUT)
    public ResponseEntity saveMetrics(@RequestBody String[][] body) {
        //todo have to refactor!!! KS
        String[][] metrics = body;
        return this.hephaestusService.saveChoosenMetrics(metrics);
    }
}
