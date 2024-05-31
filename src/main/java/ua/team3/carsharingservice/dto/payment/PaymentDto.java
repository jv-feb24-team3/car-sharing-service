package ua.team3.carsharingservice.dto.payment;

import java.math.BigDecimal;

public class PaymentDto {
    private Long id;
    private String status;
    private String type;
    private Long rentalId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amount;

}
