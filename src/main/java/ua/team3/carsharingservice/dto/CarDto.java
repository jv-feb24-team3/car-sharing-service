package ua.team3.carsharingservice.dto;

import java.math.BigDecimal;
import lombok.Data;
import ua.team3.carsharingservice.model.Car;

@Data
public class CarDto {
    private Long id;
    private String brand;
    private Car.CarType type;
    private int inventory;
    private BigDecimal dailyFee;
}
