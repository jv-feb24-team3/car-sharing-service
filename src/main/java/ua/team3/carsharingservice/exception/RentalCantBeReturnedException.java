package ua.team3.carsharingservice.exception;

public class RentalCantBeReturnedException extends RuntimeException {
    public RentalCantBeReturnedException(String message) {
        super(message);
    }
}
