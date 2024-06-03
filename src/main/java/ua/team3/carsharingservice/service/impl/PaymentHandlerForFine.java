package ua.team3.carsharingservice.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.service.PaymentHandler;

@Component("FINE")
@RequiredArgsConstructor
public class PaymentHandlerForFine implements PaymentHandler {
    @Value("${fine.multiplayer}")
    private BigDecimal fineMultiplayer;

    @Override
    public long calculateDays(Rental rental) {
        return ChronoUnit.DAYS.between(rental.getReturnDate(), rental.getActualReturnDate());
    }

    @Override
    public BigDecimal calculateAmount(BigDecimal dailyFee, long rentalDays) {
        return fineMultiplayer.multiply(dailyFee).multiply(BigDecimal.valueOf(rentalDays));
    }

    @Override
    public boolean canMakePayment(Rental rental, Optional<Payment> optionalPayment) {
        if (optionalPayment.isPresent()) {
            return false;
        }
        LocalDate actualReturnDate = rental.getActualReturnDate();
        LocalDate returnDate = rental.getReturnDate();
        return actualReturnDate != null && actualReturnDate.isAfter(returnDate);
    }
}
