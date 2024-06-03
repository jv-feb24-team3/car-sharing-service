package ua.team3.carsharingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.dto.RentalDto;
import ua.team3.carsharingservice.dto.RentalRequestDto;
import ua.team3.carsharingservice.exception.ForbiddenRentalCreationException;
import ua.team3.carsharingservice.exception.NoCarsAvailableException;
import ua.team3.carsharingservice.exception.NotValidRentalDateException;
import ua.team3.carsharingservice.exception.NotValidReturnDateException;
import ua.team3.carsharingservice.exception.RentalAlreadyReturnedException;
import ua.team3.carsharingservice.mapper.RentalMapper;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.model.User;
import ua.team3.carsharingservice.repository.CarRepository;
import ua.team3.carsharingservice.repository.RentalRepository;
import ua.team3.carsharingservice.service.RentalService;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final int DEFAULT_CAR_COUNT = 1;

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    @Override
    public List<RentalDto> getAll(User user, Pageable pageable) {
        return rentalRepository.findByUserId(user.getId(), pageable)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalDto getById(Long id, User user) {
        Rental rental = getRentalByIdForUser(id, user);
        return rentalMapper.toDto(rental);
    }

    @Override
    @Transactional
    public RentalDto create(RentalRequestDto rentalDto, User user) {
        validateRentalPeriod(rentalDto);
        validateRentalPermissionFor(user);

        Car car = getCarById(rentalDto.getCarId());
        decreaseInventoryInCar(car);

        Rental rental = rentalMapper.toModel(rentalDto);
        rental.setUser(user);
        rental.setCar(car);
        Rental savedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(savedRental);
    }

    @Override
    @Transactional
    public RentalDto returnRental(Long id, User user) {
        Rental rental = getRentalByIdForUser(id, user);
        ensureRentalNotReturned(rental);

        rental.setActualReturnDate(LocalDate.now());
        rental.getCar().setInventory(rental.getCar().getInventory() + DEFAULT_CAR_COUNT);
        Rental savedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(savedRental);
    }

    private void ensureRentalNotReturned(Rental rental) {
        if (rental.getActualReturnDate() != null) {
            throw new RentalAlreadyReturnedException(
                    "The rental with ID: " + rental.getId() + " has already been returned"
            );
        }
    }

    private void validateRentalPeriod(RentalRequestDto dto) {
        if (dto.getRentalDate().isBefore(LocalDate.now())) {
            throw new NotValidRentalDateException("Rental date can't be in past");
        }
        if (dto.getReturnDate().isBefore(dto.getRentalDate())) {
            throw new NotValidReturnDateException("Return date should be after rental date");
        }
    }

    private void validateRentalPermissionFor(User user) {
        checkOverdueRentalsFor(user);
    }

    private void checkOverdueRentalsFor(User user) {
        boolean hasOverdueRentals = rentalRepository.findByUserId(user.getId()).stream()
                .anyMatch(rental -> rental.getActualReturnDate() == null
                        && rental.getReturnDate().isBefore(LocalDate.now()));
        if (hasOverdueRentals) {
            throw new ForbiddenRentalCreationException(
                    "The user has overdue car rentals"
            );
        }
    }

    private Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a car by id: " + id
                ));
    }

    private Rental getRentalByIdForUser(Long id, User user) {
        return rentalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a rental by id: " + id
                ));
    }

    private void decreaseInventoryInCar(Car car) {
        int availableCarInventory = car.getInventory();
        if (availableCarInventory < DEFAULT_CAR_COUNT) {
            throw new NoCarsAvailableException(
                    "There are no available cars of this type, please choose another"
            );
        }
        car.setInventory(availableCarInventory - DEFAULT_CAR_COUNT);
    }
}
