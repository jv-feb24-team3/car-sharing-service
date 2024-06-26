package ua.team3.carsharingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import ua.team3.carsharingservice.dto.RentalSearchParameters;
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
            description = "Returns a paginated list of rentals for the authenticated user. "
                    + "If the user is an admin, it returns rentals for all users, "
                    + "optionally filtered by active status and user ID."
    )
    @Parameters({
            @Parameter(name = "is_active",
                    description = "Filter rentals by active status", required = false),
            @Parameter(name = "user_id",
                    description = "Filter rentals by user ID (admin only)", required = false)
    })
    @GetMapping
    public List<? extends RentalDto> getAll(@AuthenticationPrincipal User user, Pageable pageable,
                                            @Valid RentalSearchParameters searchParameters) {
        return rentalService.getAll(user, pageable, searchParameters);
    }

    @Operation(
            summary = "Return rental by id",
            description = "Returns details of rental by its id for the authenticated user. "
                    + "Admins can view any rental."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved rental details",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    @GetMapping("/{rentalId}")
    public <T extends RentalDto> T getById(@PathVariable Long rentalId,
                                           @AuthenticationPrincipal User user) {
        return rentalService.getById(rentalId, user);
    }

    @Operation(
            summary = "Create new rental",
            description = "Creates a new rental for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rental created successfully",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Car not found or no available cars"),
            @ApiResponse(responseCode = "409", description = "User has overdue rentals")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalDto create(@RequestBody @Valid RentalRequestDto rentalDto,
                            @AuthenticationPrincipal User user) {
        return rentalService.create(rentalDto, user);
    }

    @Operation(
            summary = "Close rental by id",
            description = "Closes the rental by its id for the authenticated user "
                    + "by setting the actual return date"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rental closed successfully",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Rental not found"),
            @ApiResponse(responseCode = "409", description = "Rental has already been returned")
    })
    @PostMapping("/return/{rentalId}")
    public RentalDto returnRental(@PathVariable Long rentalId, @AuthenticationPrincipal User user) {
        return rentalService.returnRental(rentalId, user);
    }
}
