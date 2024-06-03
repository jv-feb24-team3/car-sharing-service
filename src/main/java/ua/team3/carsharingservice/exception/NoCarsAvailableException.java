package ua.team3.carsharingservice.exception;

public class NoCarsAvailableException extends RuntimeException {
    public NoCarsAvailableException(String message) {
        super(message);
    }
}
