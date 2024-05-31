package ua.team3.carsharingservice.controller;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.team3.carsharingservice.dto.CarDto;
import ua.team3.carsharingservice.dto.CreateCarRequestDto;
import ua.team3.carsharingservice.service.CarService;

@Tag(name = "Car management", description = "Endpoints for managing cars")
@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @Operation(
            summary = "Create new car",
            description = "Add new car to database. Only accessible by user with the ADMIN role.",
            parameters =
            @Parameter(name = "requestDto",
                    description = "Dto containing details for creating car")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CarDto createCar(@Valid @RequestBody CreateCarRequestDto requestDto) {
        return carService.addCar(requestDto);
    }

    @Operation(
            summary = "Return list of cars",
            description = "Returns a paginated list of cars for every user",
            parameters =
            @Parameter(name = "pageable",
            description = "Pagination information (page number, size and sorting")
    )
    @GetMapping
    public List<CarDto> getAllCars(Pageable pageable) {
        return carService.findAllCars(pageable);
    }

    @Operation(
            summary = "Return car by id",
            description = "Return details of car by its id",
            parameters =
            @Parameter(name = "cartId",
                    description = "Id of the car to be retrieved")
    )
    @GetMapping("/{carId}")
    public CarDto getCarById(@PathVariable Long carId) {
        return carService.findCarById(carId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{carId}")
    public CarDto updateCarById(@PathVariable Long carId,
                                @Valid @RequestBody CreateCarRequestDto requestDto) {
        return carService.updateById(carId, requestDto);
    }

    @Operation(
            summary = "Delete by id",
            description = "Delete data about a car by its id. Only accessible by user role ADMIN",
            parameters =
            @Parameter(name = "carId",
                    description = "Id of the car to be deleted", required = true))
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{carId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCarById(@PathVariable Long carId) {
        carService.deleteById(carId);
    }
}
