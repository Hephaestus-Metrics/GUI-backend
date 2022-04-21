package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class ExampleMetric {
    //only for testing!!!

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Map<String, String> labels;
}
