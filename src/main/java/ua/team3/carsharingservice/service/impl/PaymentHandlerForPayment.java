package ua.team3.carsharingservice.service.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.service.PaymentHandler;

@Component("PAYMENT")
@RequiredArgsConstructor
public class PaymentHandlerForPayment implements PaymentHandler {

    @Override
    public long calculateDays(Rental rental) {
        return ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
    }

    @Override
    public BigDecimal calculateAmount(BigDecimal dailyFee, long rentalDays) {
        return dailyFee.multiply(BigDecimal.valueOf(rentalDays));
    }

    @Override
    public boolean canCreateSession(Rental rental, Payment payment) {
        return payment == null
                || payment.getSessionId() == null
                || payment.getStatus() != Payment.Status.PAID;
    }
}
