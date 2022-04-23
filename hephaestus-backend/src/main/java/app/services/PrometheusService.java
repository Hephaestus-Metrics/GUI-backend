package app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PrometheusService {

    @Value("${prometheus.address}")
    private String prometheusAddress;

    public String getPrometheusAddress() {
        if (!prometheusAddress.startsWith("http://") && !prometheusAddress.startsWith("https://")){
            return "http://" + prometheusAddress;
        }
        return prometheusAddress;
    }
}
