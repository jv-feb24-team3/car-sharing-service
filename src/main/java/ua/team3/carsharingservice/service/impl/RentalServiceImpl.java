package ua.team3.carsharingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.dto.RentalDto;
import ua.team3.carsharingservice.dto.RentalRequestDto;
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
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    @Override
    public List<RentalDto> getAll(User user) {
        return rentalRepository.findByUserId(user.getId())
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalDto getById(Long id, User user) {
        Rental rental = rentalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a rental by id: " + id
                ));
        return rentalMapper.toDto(rental);
    }

    @Override
    @Transactional
    public RentalDto create(RentalRequestDto dto, User user) {
        boolean hasActiveRentals = rentalRepository.findByUserId(user.getId()).stream()
                .anyMatch(rental -> rental.getActualReturnDate() == null
                        && rental.getReturnDate().isBefore(LocalDate.now()));

        if (hasActiveRentals) {
            throw new NoCarsAvailableException(
                    "The user has overdue car rentals"
            );
        }

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a car by id: " + dto.getCarId()
                ));

        if (dto.getRentalDate().isBefore(LocalDate.now())) {
            throw new NotValidRentalDateException("Rental date can't be in past");
        }

        if (dto.getReturnDate().isBefore(dto.getRentalDate())) {
            throw new NotValidReturnDateException("Return date should be after rental date");
        }

        int availableCarInventory = car.getInventory();
        if (availableCarInventory < 1) {
            throw new NoCarsAvailableException(
                    "There are no available cars of this type, please choose another"
            );
        }
        car.setInventory(availableCarInventory - 1);
        carRepository.save(car);

        Rental rental = rentalMapper.toModel(dto);
        rental.setUser(user);
        rental.setCar(car);
        Rental savedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(savedRental);
    }

    @Override
    @Transactional
    public RentalDto returnRental(Long id, User user) {
        Rental rental = rentalRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a rental by id: " + id
                ));

        Car car = carRepository.findById(rental.getCar().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a car by id: " + rental.getCar().getId()
                ));

        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);

        if (rental.getActualReturnDate() != null) {
            throw new RentalAlreadyReturnedException(
                    "The rental with ID: " + id + " has already been returned"
            );
        }
        rental.setActualReturnDate(LocalDate.now());
        Rental savedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(savedRental);
    }
}
