package ua.team3.carsharingservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalRequestDto {
    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rentalDate;
    @NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    @NotNull
    @Positive
    private Long carId;
}
