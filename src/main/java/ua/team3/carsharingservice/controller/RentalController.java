package ua.team3.carsharingservice.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.team3.carsharingservice.dto.RentalDto;
import ua.team3.carsharingservice.dto.RentalRequestDto;
import ua.team3.carsharingservice.model.User;
import ua.team3.carsharingservice.service.RentalService;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @GetMapping
    public List<RentalDto> getAll(@AuthenticationPrincipal User user) {
        return rentalService.getAll(user);
    }

    @GetMapping("/{rentalId}")
    public RentalDto getById(@PathVariable Long rentalId, @AuthenticationPrincipal User user) {
        return rentalService.getById(rentalId, user);
    }

    @PostMapping
    public RentalDto create(@RequestBody @Valid RentalRequestDto rentalDto,
                            @AuthenticationPrincipal User user) {
        return rentalService.create(rentalDto, user);
    }

    @PostMapping("/return/{rentalId}")
    public RentalDto returnRental(@PathVariable Long rentalId, @AuthenticationPrincipal User user) {
        return rentalService.returnRental(rentalId, user);
    }
}
