package ua.team3.carsharingservice.service.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.service.PaymentHandler;

@Component("PAYMENT")
@RequiredArgsConstructor
public class PaymentHadlerForPayment implements PaymentHandler {

    @Override
    public long calculateDays(Rental rental) {
        return ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
    }

    @Override
    public BigDecimal calculateAmount(BigDecimal dailyFee, long rentalDays) {
        return dailyFee.multiply(BigDecimal.valueOf(rentalDays));
    }

    @Override
    public boolean canMakePayment(Rental rental, Optional<Payment> optionalPayment) {
        return optionalPayment.isEmpty();
    }
}
