package ua.team3.carsharingservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.team3.carsharingservice.dto.CarWithoutInventoryDto;
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

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    private static final Long RENTAL_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long CAR_ID = 1L;
    private static final int CAR_INVENTORY = 5;

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private RentalMapper rentalMapper;
    @InjectMocks
    private RentalServiceImpl rentalService;

    private User user;
    private Car car;
    private Rental rental;
    private RentalRequestDto rentalRequestDto;
    private RentalDto rentalDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);

        car = new Car();
        car.setId(CAR_ID);
        car.setInventory(CAR_INVENTORY);

        rental = new Rental();
        rental.setId(RENTAL_ID);
        rental.setUser(user);
        rental.setCar(car);

        LocalDate rentalDate = LocalDate.now();
        LocalDate returnDate = rentalDate.plusDays(4);
        rental.setRentalDate(rentalDate);
        rental.setReturnDate(returnDate);

        rentalRequestDto = new RentalRequestDto();
        rentalRequestDto.setCarId(CAR_ID);
        rentalRequestDto.setRentalDate(rentalDate);
        rentalRequestDto.setReturnDate(returnDate);

        rentalDto = new RentalDto();
        rentalDto.setId(RENTAL_ID);
        CarWithoutInventoryDto carWithoutInventoryDto = new CarWithoutInventoryDto();
        carWithoutInventoryDto.setId(CAR_ID);
        rentalDto.setCar(carWithoutInventoryDto);
        rentalDto.setRentalDate(rentalDate);
        rentalDto.setReturnDate(returnDate);
    }

    @Test
    @DisplayName("Create new rental with valid values")
    void createRental_validData_success() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(rentalMapper.toModel(any(RentalRequestDto.class))).thenReturn(rental);
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        Rental expectedRental = new Rental();
        expectedRental.setId(RENTAL_ID);
        expectedRental.setRentalDate(rentalRequestDto.getRentalDate());
        expectedRental.setReturnDate(rentalRequestDto.getReturnDate());
        expectedRental.setCar(car);

        RentalDto createdRental = rentalService.create(rentalRequestDto, user);
        assertNotNull(createdRental);
        EqualsBuilder.reflectionEquals(expectedRental, createdRental);
        assertEquals(CAR_INVENTORY - 1, car.getInventory());

        verify(carRepository).findById(rentalRequestDto.getCarId());
        verify(rentalRepository).save(rental);
    }

    @Test
    void createRental_invalidRentalDate_exception() {
        rentalRequestDto.setRentalDate(LocalDate.now().minusDays(1));

        assertThrows(NotValidRentalDateException.class, () -> {
            rentalService.create(rentalRequestDto, user);
        });
    }

    @Test
    void createRental_invalidReturnDate_exception() {
        rentalRequestDto.setReturnDate(LocalDate.now().minusDays(1));

        assertThrows(NotValidReturnDateException.class, () -> {
            rentalService.create(rentalRequestDto, user);
        });
    }

    @Test
    void createRental_noCarsAvailable_exception() {
        car.setInventory(0);
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        assertThrows(NoCarsAvailableException.class, () -> {
            rentalService.create(rentalRequestDto, user);
        });

        verify(carRepository).findById(rentalRequestDto.getCarId());
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void createRental_overdueRentals_exception() {
        Rental overdueRental = new Rental();
        overdueRental.setUser(user);
        overdueRental.setRentalDate(LocalDate.now().minusDays(10));
        overdueRental.setReturnDate(LocalDate.now().minusDays(5));
        overdueRental.setActualReturnDate(null);

        when(rentalRepository.findByUserId(user.getId())).thenReturn(List.of(overdueRental));

        assertThrows(ForbiddenRentalCreationException.class, () -> {
            rentalService.create(rentalRequestDto, user);
        });

        verify(rentalRepository).findByUserId(user.getId());
    }

    @Test
    void returnRental_validData_success() {
        rental.setActualReturnDate(null);

        when(rentalRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        RentalDto result = rentalService.returnRental(RENTAL_ID, user);

        assertNotNull(result);
        assertEquals(rentalDto.getId(), result.getId());
        assertEquals(CAR_INVENTORY + 1, car.getInventory());
        verify(rentalRepository).findByIdAndUserId(RENTAL_ID, user.getId());
        verify(rentalRepository).save(rental);
    }

    @Test
    void returnRental_AlreadyReturned_exception() {
        rental.setActualReturnDate(LocalDate.now());

        when(rentalRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(rental));

        assertThrows(RentalAlreadyReturnedException.class, () -> {
            rentalService.returnRental(RENTAL_ID, user);
        });
    }

    @Test
    void getAllRentals_validData_success() {
        Pageable pageable = PageRequest.of(0, 10);
        when(rentalRepository.findByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(rental));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        List<RentalDto> rentals = rentalService.getAll(user, pageable);

        assertNotNull(rentals);
        assertEquals(1, rentals.size());
        verify(rentalRepository).findByUserId(user.getId(), pageable);
    }

    @Test
    void getRentalById_validData_success() {
        when(rentalRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        RentalDto result = rentalService.getById(RENTAL_ID, user);

        assertNotNull(result);
        assertEquals(rentalDto.getId(), result.getId());
        verify(rentalRepository).findByIdAndUserId(RENTAL_ID, user.getId());
    }

    @Test
    void getRentalById_NotFound_exception() {
        when(rentalRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            rentalService.getById(RENTAL_ID, user);
        });
    }
}
