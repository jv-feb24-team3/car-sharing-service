package ua.team3.carsharingservice.service;

import java.math.BigDecimal;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.model.Rental;

public interface PaymentHandler {
    long calculateDays(Rental rental);

    BigDecimal calculateAmount(BigDecimal dailyFee, long rentalDays);

    boolean canCreateSession(Rental rental, Payment payment);
}
