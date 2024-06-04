package ua.team3.carsharingservice.dto.stripe.payment;

import java.math.BigDecimal;

public record PaymentDto(
        Long id,
        String status,
        String type,
        Long rentalId,
        String sessionUrl,
        String sessionId,
        BigDecimal amount
) {
}
