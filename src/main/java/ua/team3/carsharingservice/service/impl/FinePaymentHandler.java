package ua.team3.carsharingservice.service.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.service.PaymentHandler;

@Component("FINE")
@RequiredArgsConstructor
public class FinePaymentHandler implements PaymentHandler {
    private static final BigDecimal FAIN_MULTIPLY = BigDecimal.valueOf(1.3);

    @Override
    public long calculateDays(Rental rental) {
        return ChronoUnit.DAYS.between(rental.getActualReturnDate(), rental.getReturnDate());
    }

    @Override
    public BigDecimal calculateAmount(BigDecimal dailyFee, long rentalDays) {
        return FAIN_MULTIPLY.multiply(dailyFee).multiply(BigDecimal.valueOf(rentalDays));
    }
}
