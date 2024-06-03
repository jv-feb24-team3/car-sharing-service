package ua.team3.carsharingservice.exception;

public class NotValidRentalDateException extends RuntimeException {
    public NotValidRentalDateException(String message) {
        super(message);
    }
}
