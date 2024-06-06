package ua.team3.carsharingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.dto.RentalDto;
import ua.team3.carsharingservice.dto.RentalRequestDto;
import ua.team3.carsharingservice.dto.RentalSearchParameters;
import ua.team3.carsharingservice.exception.ForbiddenRentalCreationException;
import ua.team3.carsharingservice.exception.NoCarsAvailableException;
import ua.team3.carsharingservice.exception.NotValidRentalDateException;
import ua.team3.carsharingservice.exception.NotValidReturnDateException;
import ua.team3.carsharingservice.exception.RentalCantBeReturnedException;
import ua.team3.carsharingservice.mapper.RentalMapper;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.model.User;
import ua.team3.carsharingservice.repository.CarRepository;
import ua.team3.carsharingservice.repository.RentalRepository;
import ua.team3.carsharingservice.repository.specification.rental.RentalSpecificationBuilder;
import ua.team3.carsharingservice.service.PaymentService;
import ua.team3.carsharingservice.service.RentalService;
import ua.team3.carsharingservice.telegram.service.NotificationService;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final int DEFAULT_CAR_COUNT = 1;

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final NotificationService notificationService;
    private final PaymentService paymentService;
    private final RentalSpecificationBuilder specificationBuilder;

    @Override
    public List<? extends RentalDto> getAll(User user, Pageable pageable,
                                            RentalSearchParameters searchParameters) {
        boolean isUserAdmin = user.isAdmin();
        if (!isUserAdmin) {
            searchParameters.setUserIds(new Long[]{user.getId()});
        }
        Specification<Rental> spec = specificationBuilder.build(searchParameters);

        List<Rental> rentals = rentalRepository.findAll(spec, pageable).getContent();
        return rentals.stream()
                .map(getRentalMapper(isUserAdmin))
                .toList();
    }

    @Override
    public <T extends RentalDto> T getById(Long id, User user) {
        Rental rental = getRentalByIdForUser(id, user);
        return (T) getRentalMapper(user.isAdmin()).apply(rental);
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
        rental.setStatus(Rental.Status.PENDING);
        Rental savedRental = rentalRepository.save(rental);

        paymentService.createPaymentForRental(savedRental);
        notificationService.sendRentalCreatedNotification(savedRental);
        return rentalMapper.toDto(savedRental);
    }

    @Override
    @Transactional
    public RentalDto returnRental(Long id, User user) {
        Rental rental = getRentalByIdForUser(id, user);
        ensureRentalCanBeReturned(rental);

        rental.setActualReturnDate(LocalDate.now());
        rental.getCar().setInventory(rental.getCar().getInventory() + DEFAULT_CAR_COUNT);
        //TODO: Possibly move to payment service
        rental.setStatus(Rental.Status.COMPLETED);
        Rental savedRental = rentalRepository.save(rental);
        paymentService.createFinePaymentIfNeeded(savedRental);
        return rentalMapper.toDto(savedRental);
    }

    private Function<Rental, ? extends RentalDto> getRentalMapper(boolean isUserAdmin) {
        return isUserAdmin
                ? rentalMapper::toDtoForAdmin
                : rentalMapper::toDto;
    }

    @Transactional
    @Scheduled(timeUnit = TimeUnit.DAYS, fixedRate = 1)
    public void getOverdueRentals() {
        List<Rental> overDueRentals = rentalRepository.getOverdueRentals();
        if (!overDueRentals.isEmpty()) {
            for (Rental overDueRental : overDueRentals) {
                notificationService.sendOverdueRentalsNotification(overDueRental);
            }
        } else {
            notificationService.sendNoOverdueRentalsNotification();
        }
    }

    private void ensureRentalCanBeReturned(Rental rental) {
        if (rental.getStatus() == Rental.Status.CANCELLED) {
            throw new RentalCantBeReturnedException(
                    "The rental with ID: " + rental.getId() + " was been cancelled"
            );
        }
        if (rental.getActualReturnDate() != null) {
            throw new RentalCantBeReturnedException(
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
        checkUnreturnedCarsFor(user);
        ensureUserHasNoDebt(user);
    }

    private void ensureUserHasNoDebt(User user) {
        List<Rental.Status> debtStatuses = Arrays.asList(
                Rental.Status.PENDING,
                Rental.Status.OVERDUE
        );
        List<Rental> rentalsWithDebt = rentalRepository.findByStatusInAndUser(debtStatuses, user);
        if (!rentalsWithDebt.isEmpty()) {
            String rentalIds = rentalsWithDebt.stream()
                    .map(Rental::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new ForbiddenRentalCreationException(
                    "The user has debt the rental with the following rental ids: "
                            + rentalIds
            );
        }
    }

    private void checkUnreturnedCarsFor(User user) {
        boolean hasUnreturnedCars = rentalRepository.findByUserId(user.getId()).stream()
                .anyMatch(rental -> rental.getActualReturnDate() == null
                        && rental.getReturnDate().isBefore(LocalDate.now()));
        if (hasUnreturnedCars) {
            throw new ForbiddenRentalCreationException(
                    "The user has unreturned cars"
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
        boolean isUserAdmin = user.isAdmin();
        Optional<Rental> optionalRental = isUserAdmin
                ? rentalRepository.findById(id)
                : rentalRepository.findByIdAndUserId(id, user.getId());

        return optionalRental.orElseThrow(
                () -> new EntityNotFoundException("Can't find a rental by id: " + id)
        );
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
