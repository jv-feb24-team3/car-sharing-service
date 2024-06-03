package ua.team3.carsharingservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalDto {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private Long id;
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate rentalDate;
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate returnDate;
    @JsonFormat(pattern = DATE_PATTERN)
    private LocalDate actualReturnDate;
    private CarWithoutInventoryDto car;
}
