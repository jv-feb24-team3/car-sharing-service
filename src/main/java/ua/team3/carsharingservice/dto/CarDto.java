package ua.team3.carsharingservice.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarDto {
    private Long id;
    private String brand;
    private String type;
    private int inventory;
    private BigDecimal dailyFee;
}
