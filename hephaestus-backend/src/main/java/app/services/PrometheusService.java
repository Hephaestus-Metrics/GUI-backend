package app.services;

import app.exceptions.PrometheusServiceException;
import app.model.Filters;
import app.model.SelectedMetrics;
import app.volume.VolumeManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
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
public class PrometheusService {

    private final String prometheusAddress;

    private final RestTemplate restTemplate;

    private List<Filters> selectedQueries;

    private final VolumeManager volumeManager;

    private final QueryBuilderService queryBuilderService;


    public PrometheusService(RestTemplateBuilder restTemplateBuilder, @Value("${prometheus.address}") String prometheusAddress, VolumeManager volumeManager, QueryBuilderService queryBuilderService) {
        this.restTemplate = restTemplateBuilder.build();
        if (!prometheusAddress.startsWith("http://") && !prometheusAddress.startsWith("https://")){
            this.prometheusAddress = "http://" + prometheusAddress;
        } else {
            this.prometheusAddress = prometheusAddress;
        }
        this.queryBuilderService = queryBuilderService;
        this.volumeManager = volumeManager;
        this.selectedQueries = volumeManager.loadMetrics();
        log.info("Set prometheus address to {}", prometheusAddress);
    }

    public String getPrometheusAddress() {
        return prometheusAddress;
    }

    public String getLabelsJson() {
        String labelsAddress = getPrometheusAddress() + "/api/v1/labels";
        try {
            return restTemplate.getForObject(labelsAddress, String.class);
        } catch(Exception ex) {
          throw new PrometheusServiceException(
                  "Unexpected exception occurred while loading labels: " + ex.getMessage());
        }
    }

    public String getLabelValuesJson(String label) {
        try {
            return restTemplate.getForObject(
                    getPrometheusAddress() + "/api/v1/label/{my_label}/values", String.class, label);
        } catch(Exception ex) {
            throw new PrometheusServiceException(
                    "Unexpected exception occurred while loading labels' values: " + ex.getMessage());
        }
    }

    public ResponseEntity saveMetrics(Filters[] metrics) {
        selectedQueries = Arrays.stream(metrics).collect(Collectors.toList());
        return this.volumeManager.saveMetrics(metrics);
    }

    public SelectedMetrics getSelectedMetrics() {
        if (selectedQueries != null) {
            List<String> queryResults = selectedQueries.parallelStream()
                    .map(queryBuilderService::filtersToQuery)
                    .map(this::query)
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

    public String query(String query) {
        if (query == null){
            return "{}";
        }

        return restTemplate.getForObject(
                getPrometheusAddress() + "/api/v1/query?query={my_query}",
                String.class,
                query);
    }

}
