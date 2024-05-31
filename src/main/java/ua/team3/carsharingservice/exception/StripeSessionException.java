package ua.team3.carsharingservice.exception;

public class StripeSessionException extends RuntimeException {
    public StripeSessionException(String message, Throwable e) {
        super(message, e);
    }
}
