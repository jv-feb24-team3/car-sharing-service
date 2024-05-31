package ua.team3.carsharingservice.dto.stripe;

import lombok.Data;

@Data
public class StripeTokenDto {
    private String cardNumber;
    private Integer expMonth;
    private Integer expYear;
    private Integer cvc;
    private String token;
    private String username;
    private boolean success;
}
