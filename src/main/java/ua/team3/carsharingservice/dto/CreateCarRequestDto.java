package ua.team3.carsharingservice.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateCarRequestDto {
    private String brand;
    private String type;
    private int inventory;
    private BigDecimal dailyFree;
}
