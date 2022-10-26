package app.controllers;

import app.model.SelectedFilters;
import app.model.SelectedQuery;
import app.services.PrometheusService;
import app.services.QueryBuilderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import conf.Configuration;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
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
    public String postQueryFilters(@RequestBody String filtersString) throws JsonProcessingException {
        log.info("String " + filtersString);
        SelectedQuery filters = objectMapper.readValue(filtersString, SelectedFilters.class);
        log.info("Request to perform query for filters: " + filters + ", (query string: " + filters.getQueryString() + ")");
        return prometheusService.query(filters.getQueryString());
    }

}
