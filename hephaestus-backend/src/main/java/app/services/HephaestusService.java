package app.services;

import app.model.Filters;
import app.model.SelectedMetrics;
import app.providers.QueryProvider;
import app.volume.VolumeManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class HephaestusService {

    private final VolumeManager volumeManager;

    private final QueryBuilderService queryBuilderService;

    private final PrometheusService prometheusService;

    private List<Filters> selectedQueries;

    private final RestTemplate restTemplate;

    public HephaestusService(VolumeManager volumeManager, QueryBuilderService queryBuilderService, PrometheusService prometheusService, RestTemplateBuilder restTemplateBuilder) {
        this.queryBuilderService = queryBuilderService;
        this.prometheusService = prometheusService;
        this.restTemplate = restTemplateBuilder.build();
        this.volumeManager = volumeManager;
        this.selectedQueries = volumeManager.loadMetrics();
    }

    public ResponseEntity saveMetrics(Filters[] metrics) {
        selectedQueries = Arrays.stream(metrics).collect(Collectors.toList());
        return this.volumeManager.saveMetrics(metrics);
    }

    public SelectedMetrics getSelectedMetrics() {
        if (selectedQueries != null) {
            List<String> queryResults = selectedQueries.parallelStream()
                    .map(queryBuilderService::filtersToQuery)
                    .map(query -> QueryProvider.query(query, prometheusService.getPrometheusAddress(), restTemplate))
                    .collect(Collectors.toList());
            log.info("Returning {} selected metrics metrics", queryResults.size());
            return new SelectedMetrics(queryResults);
        } else {
            log.info("No metrics selected - returning an empty list");
            return new SelectedMetrics(new ArrayList<>());
        }
    }

    public List<Filters> getSavedMetrics() {
        log.info("Returning {} saved metrics", this.selectedQueries.size());
        return this.selectedQueries;
    }

}
