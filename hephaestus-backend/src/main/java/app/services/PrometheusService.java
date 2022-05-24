package app.services;

import conf.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PrometheusService {

    private final String prometheusAddress;

    private final RestTemplate restTemplate;

    public PrometheusService(RestTemplateBuilder restTemplateBuilder, @Value(Configuration.PROMETHEUS) String prometheusAddress) {
        this.restTemplate = restTemplateBuilder.build();
        if (!prometheusAddress.startsWith("http://") && !prometheusAddress.startsWith("https://")){
            this.prometheusAddress = "http://" + prometheusAddress;
        } else {
            this.prometheusAddress = prometheusAddress;
        }
    }

    public String getPrometheusAddress() {
        return prometheusAddress;
    }

    public String getLabelsJson() {
        return restTemplate.getForObject(getPrometheusAddress() + "/api/v1/labels", String.class);
    }

    public String getLabelValuesJson(String label) {
        return restTemplate.getForObject(getPrometheusAddress() + "/api/v1/label/{my_label}/values", String.class, label);
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
