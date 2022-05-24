package conf;

public class Configuration {
    public static final String GUI_ORIGINS = "http://localhost:4200";
    public static final String PROMETHEUS = "prometheus:9090";
    public static final String VOLUME_PATH = System.getProperty("user.dir") + "/../savedMetrics/Metrics.json";
}
