package ua.team3.carsharingservice.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ua.team3.carsharingservice.dto.RentalDto;
import ua.team3.carsharingservice.dto.RentalRequestDto;
import ua.team3.carsharingservice.model.User;

public interface RentalService {
    List<RentalDto> getAll(User user, Pageable pageable);

    RentalDto getById(Long id, User user);

    RentalDto create(RentalRequestDto dto, User user);

    RentalDto returnRental(Long id, User user);
}