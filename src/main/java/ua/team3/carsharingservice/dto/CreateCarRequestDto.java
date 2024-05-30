package ua.team3.carsharingservice.dto;

import java.math.BigDecimal;
import lombok.Data;
import ua.team3.carsharingservice.model.Car;

@Data
public class CreateCarRequestDto {
    private String brand;
    private Car.CarType type;
    private int inventory;
    private BigDecimal dailyFree;
}
