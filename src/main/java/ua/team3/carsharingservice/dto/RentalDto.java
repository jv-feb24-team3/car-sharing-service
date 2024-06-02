package ua.team3.carsharingservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rentalDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualReturnDate;
    private CarWithoutInventoryDto car;
}
