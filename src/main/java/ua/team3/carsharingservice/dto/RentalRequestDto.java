package ua.team3.carsharingservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalRequestDto {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @NotNull(message = "Rental date may not be empty")
    @JsonFormat(pattern = DATE_PATTERN)
    @Schema(type = "string", example = "2024-06-29")
    private LocalDate rentalDate;
    @NotNull(message = "Return date may not be empty")
    @JsonFormat(pattern = DATE_PATTERN)
    @Schema(type = "string", example = "2024-06-30")
    private LocalDate returnDate;
    @NotNull(message = "Car ID may not be empty")
    @Positive(message = "Car ID should be greater than 0")
    private Long carId;
}
