package ua.team3.carsharingservice.service.impl;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.service.PaymentHandler;

@Component("PAYMENT")
@RequiredArgsConstructor
public class PaymentHandlerForPayment implements PaymentHandler {
    private static final String BILLING_TEMPLATE =
            "Rental for %s, %s to %s (%d %s, $%d per day)";

    @Override
    public long calculateDays(Rental rental) {
        return ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
    }

    @Override
    public BigDecimal calculateAmount(BigDecimal dailyFee, long rentalDays) {
        return dailyFee.multiply(BigDecimal.valueOf(rentalDays));
    }

    @Override
    public String formBillingDetails(Rental rental) {
        Car car = rental.getCar();
        String carName = car.getBrand();
        long dailyFee = car.getDailyFee().longValue();
        String startDate = rental.getRentalDate().toString();
        String endDate = rental.getReturnDate().toString();
        long daysCount = calculateDays(rental);
        return String.format(BILLING_TEMPLATE,
                carName,
                startDate,
                endDate,
                daysCount,
                daysCount == 1 ? "day" : "days",
                dailyFee);
    }
}
