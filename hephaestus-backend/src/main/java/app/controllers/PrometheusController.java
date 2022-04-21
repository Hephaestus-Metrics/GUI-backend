package app.controllers;

import conf.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
public class PrometheusController {
    //todo
}
