package app.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class SelectedMetrics {

    private final List<RawMetric> rawMetrics;

}
