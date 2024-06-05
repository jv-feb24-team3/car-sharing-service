package ua.team3.carsharingservice.dto;

import lombok.Data;

@Data
public class RentalForUserDto extends RentalDto {
    private CarWithoutInventoryDto car;
}
