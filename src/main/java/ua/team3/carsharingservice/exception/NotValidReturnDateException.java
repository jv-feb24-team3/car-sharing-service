package ua.team3.carsharingservice.exception;

public class NotValidReturnDateException extends RuntimeException {
    public NotValidReturnDateException(String message) {
        super(message);
    }
}
