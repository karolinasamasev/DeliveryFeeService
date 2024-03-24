package ee.karu.deliveryfeeservice.exception;

public class NoSuchObservationDataException extends RuntimeException {
    public NoSuchObservationDataException() {
        super("Observation data not found");
    }
}
