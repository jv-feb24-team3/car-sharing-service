package ua.team3.carsharingservice.service.impl;

import static ua.team3.carsharingservice.util.StripeConst.FINE_MULTIPLAYER;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.service.PaymentHandler;

@Component("FINE")
@RequiredArgsConstructor
public class PaymentHandlerForFine implements PaymentHandler {
    private static final String BILLING_TEMPLATE =
            "Fine for %s, %s to %s (%d %s, $%d per day), Reason: Late return";

    @Override
    public long calculateDays(Rental rental) {
        return ChronoUnit.DAYS.between(rental.getReturnDate(), rental.getActualReturnDate());
    }

    @Override
    public BigDecimal calculateAmount(BigDecimal dailyFee, long rentalDays) {
        return FINE_MULTIPLAYER.multiply(dailyFee).multiply(BigDecimal.valueOf(rentalDays));
    }

    @Override
    public String formBillingDetails(Rental rental) {
        Car car = rental.getCar();
        String carName = car.getBrand();
        long dailyFee = car.getDailyFee().multiply(FINE_MULTIPLAYER).longValue();
        String startDate = rental.getReturnDate().toString();
        String endDate = rental.getActualReturnDate().toString();
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
