package app.controllers;

import app.model.Filters;
import app.services.PrometheusService;
import app.services.QueryBuilderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import conf.Configuration;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("prometheus")
@CrossOrigin(origins = Configuration.GUI_ORIGINS)
public class PrometheusController {

    private final PrometheusService prometheusService;
    private final QueryBuilderService queryBuilderService;
    private final ObjectMapper objectMapper;

    public PrometheusController(PrometheusService prometheusService, QueryBuilderService queryBuilderService, ObjectMapper objectMapper) {
        this.prometheusService = prometheusService;
        this.queryBuilderService = queryBuilderService;
        this.objectMapper = objectMapper;
    }

    @RequestMapping(value = "/address", method = RequestMethod.GET, produces = "application/json")
    public String getAddress() throws JsonProcessingException {
        return objectMapper.writeValueAsString(prometheusService.getPrometheusAddress());
    }

    @RequestMapping(value = "/labels", method = RequestMethod.GET, produces = "application/json")
    public String getLabels() {
        return prometheusService.getLabelsJson();
    }

    @RequestMapping(value = "/values", method = RequestMethod.GET, produces = "application/json")
    public String getLabelValues(@RequestParam("label") String label) {
        return prometheusService.getLabelValuesJson(label);
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json")
    public String postQuery(@RequestBody String query) {
        return prometheusService.query(query);
    }

    @RequestMapping(value = "/query/filters", method = RequestMethod.POST, produces = "application/json")
    public String postQuery(@RequestBody Filters filters) {
        return prometheusService.query(queryBuilderService.filtersToQuery(filters.getValues()));
    }

}
