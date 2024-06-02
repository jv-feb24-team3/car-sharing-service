package ua.team3.carsharingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.team3.carsharingservice.dto.RentalDto;
import ua.team3.carsharingservice.dto.RentalRequestDto;
import ua.team3.carsharingservice.model.User;
import ua.team3.carsharingservice.service.RentalService;

@Tag(name = "Rental management", description = "Endpoints for managing rentals")
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @Operation(
            summary = "Return list of rentals",
            description = "Returns a paginated list of rentals for every user"
    )
    @GetMapping
    public List<RentalDto> getAll(@AuthenticationPrincipal User user, Pageable pageable) {
        return rentalService.getAll(user, pageable);
    }

    @Operation(
            summary = "Return rental by id",
            description = "Return details of rental by its id"
    )
    @GetMapping("/{rentalId}")
    public RentalDto getById(@PathVariable Long rentalId, @AuthenticationPrincipal User user) {
        return rentalService.getById(rentalId, user);
    }

    @Operation(summary = "Create new rental")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalDto create(@RequestBody @Valid RentalRequestDto rentalDto,
                            @AuthenticationPrincipal User user) {
        return rentalService.create(rentalDto, user);
    }

    @Operation(summary = "Close rental by id")
    @PostMapping("/return/{rentalId}")
    public RentalDto returnRental(@PathVariable Long rentalId, @AuthenticationPrincipal User user) {
        return rentalService.returnRental(rentalId, user);
    }
}
