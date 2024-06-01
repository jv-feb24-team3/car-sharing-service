package ua.team3.carsharingservice.dto.stripe.session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SessionCreateDto {
    @NotNull
    @Positive
    private Long rentalId;
    @NotBlank
    private String paymentType;
}
