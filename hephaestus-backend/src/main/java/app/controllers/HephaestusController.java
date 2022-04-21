package app.controllers;

import conf.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.HephaestusService;

@RestController
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
public class HephaestusController {
    private final HephaestusService hephaestusService;

    public HephaestusController(HephaestusService hephaestusService) {
        this.hephaestusService = hephaestusService;
    }

    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public ResponseEntity getMetrics() {
        return this.hephaestusService.getMetrics();
    }
}
