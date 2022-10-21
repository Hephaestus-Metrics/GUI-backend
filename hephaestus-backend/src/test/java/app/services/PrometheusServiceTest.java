package app.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.web.client.RestTemplateBuilder;

import static org.junit.jupiter.api.Assertions.*;


import java.util.stream.Stream;

public class PrometheusServiceTest {
    private static final String INVALID_ADDRESS = "127.0.0.1:12345";

    @Test
    void invalidAddressTest() {
        //given
        PrometheusService prometheusService = new PrometheusService(new RestTemplateBuilder(), INVALID_ADDRESS);

        //when
        String actualAddress = prometheusService.getPrometheusAddress();

        //then
        assertEquals("http://" + INVALID_ADDRESS, actualAddress);
    }

    @ParameterizedTest
    @MethodSource("validAddressSource")
    void validAddressTest(String address) {
        //given
        PrometheusService prometheusService = new PrometheusService(new RestTemplateBuilder(), address);

        //when
        String actualAddress = prometheusService.getPrometheusAddress();

        //then
        assertEquals(address, actualAddress);
    }

    private static Stream<String> validAddressSource() {
        return Stream.of(
                "http://" + INVALID_ADDRESS,
                "https://" + INVALID_ADDRESS
        );
    }
}
