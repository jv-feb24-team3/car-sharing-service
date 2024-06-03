package ua.team3.carsharingservice.exception;

public class NotificationSendingException extends RuntimeException {
    public NotificationSendingException(String message) {
        super(message);
    }
}
