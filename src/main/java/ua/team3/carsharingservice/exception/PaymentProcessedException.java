package ua.team3.carsharingservice.exception;

public class PaymentProcessedException extends RuntimeException {
    public PaymentProcessedException(String message) {
        super(message);
    }
}
