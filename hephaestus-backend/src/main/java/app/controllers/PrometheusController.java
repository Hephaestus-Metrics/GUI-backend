package app.controllers;

import app.services.PrometheusService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import conf.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("prometheus")
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
public class PrometheusController {

    private final PrometheusService prometheusService;
    private final ObjectMapper objectMapper;

    public PrometheusController(PrometheusService prometheusService, ObjectMapper objectMapper) {
        this.prometheusService = prometheusService;
        this.objectMapper = objectMapper;
    }

    @RequestMapping("/address")
    public String getAddress() throws JsonProcessingException {
        return objectMapper.writeValueAsString(prometheusService.getPrometheusAddress());
    }

}
