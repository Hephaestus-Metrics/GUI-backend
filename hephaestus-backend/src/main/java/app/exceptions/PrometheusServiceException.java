package app.exceptions;

public class PrometheusServiceException extends RuntimeException{

    public PrometheusServiceException (String message) {
        super(message);
    }
}
