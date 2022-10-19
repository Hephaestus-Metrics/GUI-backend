package app.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RawMetric {

    private final String tag;
    private final String metric; // metric value as string, as returned by Prometheus

}
