package ua.team3.carsharingservice.service.impl.payment;

import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.service.BillingFormatter;

@Component
public class PaymentBillingFormatter implements BillingFormatter {
    private static final String BILLING_TEMPLATE =
            "Rental for %s, %s to %s (%d %s, $%d per day)";

    @Override
    public String formBillingDetails(String carName,
                                     String startDate,
                                     String endDate,
                                     long daysCount,
                                     long dailyFee) {
        return String.format(BILLING_TEMPLATE,
                carName,
                startDate,
                endDate,
                daysCount,
                daysCount == 1 ? "day" : "days",
                dailyFee);
    }
}
