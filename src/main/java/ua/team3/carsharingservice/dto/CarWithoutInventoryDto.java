package ua.team3.carsharingservice.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarWithoutInventoryDto {
    private Long id;
    private String brand;
    private String type;
    private BigDecimal dailyFee;
}
