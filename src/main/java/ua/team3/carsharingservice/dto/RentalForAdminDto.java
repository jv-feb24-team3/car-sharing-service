package ua.team3.carsharingservice.dto;

import lombok.Data;

@Data
public class RentalForAdminDto extends RentalDto {
    private Long userId;
}
