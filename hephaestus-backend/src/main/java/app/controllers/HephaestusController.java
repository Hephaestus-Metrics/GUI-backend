package app.controllers;

import app.model.SelectedQuery;
import app.services.HephaestusService;
import conf.Configuration;
import io.github.hephaestusmetrics.model.queryresults.RawQueryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hephaestus")
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
@Log4j2
@RequiredArgsConstructor
public class HephaestusController {

    private final HephaestusService hephaestusService;

    @RequestMapping(value = "/metrics/save", method = RequestMethod.PUT)
    public ResponseEntity<Void> saveMetrics(@RequestBody String body) {
        return hephaestusService.saveMetrics(body);
    }

    @RequestMapping(value = "/metrics/selected", method = RequestMethod.GET)
    public List<RawQueryResult> getSelected() {
        return hephaestusService.getSelected();
    }

    @RequestMapping(value = "/metrics/saved", method = RequestMethod.GET, produces = "application/json")
    public List<SelectedQuery> getSaved() {
        return hephaestusService.getSaved();
    }

}


