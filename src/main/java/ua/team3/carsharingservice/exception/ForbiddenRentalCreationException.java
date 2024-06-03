package ua.team3.carsharingservice.exception;

public class ForbiddenRentalCreationException extends RuntimeException {
    public ForbiddenRentalCreationException(String message) {
        super(message);
    }
}
