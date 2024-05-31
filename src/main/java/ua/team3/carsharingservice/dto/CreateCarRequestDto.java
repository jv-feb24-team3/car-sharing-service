package ua.team3.carsharingservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateCarRequestDto {
    @NotBlank(message = "Brand may not be empty")
    private String brand;
    @NotBlank(message = "Type may")
    private String type;
    @Min(value = 1, message = "You can't add less than 1")
    private int inventory;
    @NotBlank(message = "Daily fee may not be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Daily fee must be greater than zero")
    private BigDecimal dailyFee;
}
