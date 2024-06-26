package ua.team3.carsharingservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.team3.carsharingservice.dto.CarDto;
import ua.team3.carsharingservice.dto.CarWithoutInventoryDto;
import ua.team3.carsharingservice.dto.RentalDto;
import ua.team3.carsharingservice.dto.RentalForAdminDto;
import ua.team3.carsharingservice.dto.RentalForUserDto;
import ua.team3.carsharingservice.dto.RentalRequestDto;
import ua.team3.carsharingservice.dto.RentalSearchParameters;
import ua.team3.carsharingservice.exception.ForbiddenRentalCreationException;
import ua.team3.carsharingservice.exception.NotValidRentalDateException;
import ua.team3.carsharingservice.exception.NotValidReturnDateException;
import ua.team3.carsharingservice.exception.RentalCantBeReturnedException;
import ua.team3.carsharingservice.mapper.RentalMapper;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.model.Role;
import ua.team3.carsharingservice.model.User;
import ua.team3.carsharingservice.repository.RentalRepository;
import ua.team3.carsharingservice.repository.specification.rental.RentalSpecificationBuilder;
import ua.team3.carsharingservice.service.transaction.RentalTransaction;
import ua.team3.carsharingservice.telegram.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    private static final Long RENTAL_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long USER_ADMIN_ID = 2L;
    private static final Long CAR_ID = 1L;
    private static final int CAR_INVENTORY = 5;

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private RentalTransaction rentalTransaction;
    @Mock
    private NotificationService notificationService;
    @Mock
    private RentalSpecificationBuilder specificationBuilder;
    @InjectMocks
    private RentalServiceImpl rentalService;

    private User user;
    private User adminUser;
    private Car car;
    private Rental rental;
    private RentalRequestDto rentalRequestDto;
    private RentalForUserDto rentalDto;
    private RentalForAdminDto rentalDtoForAdmin;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);

        adminUser = new User();
        adminUser.setId(USER_ADMIN_ID);
        Role roleAdmin = new Role();
        roleAdmin.setRole(Role.RoleName.ADMIN);
        adminUser.getRoles().add(roleAdmin);

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

        rentalDto = new RentalForUserDto();
        rentalDto.setId(RENTAL_ID);
        CarWithoutInventoryDto carWithoutInventoryDto = new CarWithoutInventoryDto();
        carWithoutInventoryDto.setId(CAR_ID);
        rentalDto.setCar(carWithoutInventoryDto);
        rentalDto.setRentalDate(rentalDate);
        rentalDto.setReturnDate(returnDate);

        rentalDtoForAdmin = new RentalForAdminDto();
        rentalDtoForAdmin.setId(RENTAL_ID);
        CarDto carDto = new CarDto();
        carDto.setId(CAR_ID);
        rentalDtoForAdmin.setCar(carDto);
        rentalDtoForAdmin.setRentalDate(rentalDate);
        rentalDtoForAdmin.setReturnDate(returnDate);
        rentalDtoForAdmin.setUserId(USER_ID);

        pageable = Pageable.unpaged();
    }

    @Test
    @DisplayName("Create new rental with valid values")
    void createRental_validData_success() {
        when(rentalTransaction.create(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toModel(any(RentalRequestDto.class))).thenReturn(rental);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        Rental expectedRental = new Rental();
        expectedRental.setId(RENTAL_ID);
        expectedRental.setRentalDate(rentalRequestDto.getRentalDate());
        expectedRental.setReturnDate(rentalRequestDto.getReturnDate());
        expectedRental.setCar(car);

        RentalDto createdRental = rentalService.create(rentalRequestDto, user);
        assertNotNull(createdRental);
        EqualsBuilder.reflectionEquals(expectedRental, createdRental);
    }

    @Test
    @DisplayName("Create new rental with past rental date")
    void createRental_invalidRentalDate_exception() {
        rentalRequestDto.setRentalDate(LocalDate.now().minusDays(1));

        NotValidRentalDateException exception = assertThrows(
                NotValidRentalDateException.class,
                () -> {
                    rentalService.create(rentalRequestDto, user);
            });
        assertEquals("Rental date can't be in past", exception.getMessage());
    }

    @Test
    @DisplayName("Create new rental with return date before rental date")
    void createRental_invalidReturnDate_exception() {
        rentalRequestDto.setReturnDate(LocalDate.now().minusDays(1));

        NotValidReturnDateException exception = assertThrows(
                NotValidReturnDateException.class,
                () -> {
                    rentalService.create(rentalRequestDto, user);
            });
        assertEquals("Return date should be after rental date", exception.getMessage());
    }

    @Test
    @DisplayName("Create new rental having unreturned cars")
    void createRental_unreturnedCars_exception() {
        Rental overdueRental = new Rental();
        overdueRental.setUser(user);
        overdueRental.setRentalDate(LocalDate.now().minusDays(10));
        overdueRental.setReturnDate(LocalDate.now().minusDays(5));
        overdueRental.setActualReturnDate(null);

        when(rentalRepository.findByUserId(user.getId())).thenReturn(List.of(overdueRental));

        ForbiddenRentalCreationException exception = assertThrows(
                ForbiddenRentalCreationException.class,
                () -> {
                    rentalService.create(rentalRequestDto, user);
                });
        assertEquals("The user has unreturned cars", exception.getMessage());

        verify(rentalRepository).findByUserId(user.getId());
    }

    @Test
    @DisplayName("Create new rental having payment debt")
    void createRental_debtPayment_exception() {
        Rental overdueRental = new Rental();
        overdueRental.setUser(user);
        overdueRental.setRentalDate(LocalDate.now().minusDays(10));
        overdueRental.setReturnDate(LocalDate.now().plusDays(5));
        overdueRental.setActualReturnDate(null);
        overdueRental.setStatus(Rental.Status.PENDING);

        when(rentalRepository.findByStatusInAndUser(anyList(), any(User.class)))
                .thenReturn(List.of(overdueRental));

        ForbiddenRentalCreationException exception = assertThrows(
                ForbiddenRentalCreationException.class,
                () -> {
                    rentalService.create(rentalRequestDto, user);
                });
        verify(rentalRepository).findByStatusInAndUser(anyList(), any(User.class));
    }

    @Test
    @DisplayName("Return rental with valid values")
    void returnRental_validData_success() {
        rental.setActualReturnDate(null);
        rentalDto.setActualReturnDate(LocalDate.now());

        when(rentalRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(rental));
        when(rentalTransaction.returnRental(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        RentalDto result = rentalService.returnRental(RENTAL_ID, user);

        assertNotNull(result);
        assertNotNull(result.getActualReturnDate());
        assertEquals(rentalDto.getId(), result.getId());
        assertEquals(CAR_INVENTORY + 1, car.getInventory());
        verify(rentalRepository).findByIdAndUserId(RENTAL_ID, user.getId());
    }

    @Test
    @DisplayName("Return already returned rental")
    void returnRental_AlreadyReturned_exception() {
        rental.setActualReturnDate(LocalDate.now());

        when(rentalRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(rental));

        RentalCantBeReturnedException exception = assertThrows(
                RentalCantBeReturnedException.class,
                () -> {
                    rentalService.returnRental(RENTAL_ID, user);
            });
        assertEquals("The rental with ID: " + RENTAL_ID + " has already been returned",
                exception.getMessage());
    }

    @Test
    @DisplayName("Get all rentals")
    void getAllRentals_validData_success() {
        List<Rental> rentals = Collections.singletonList(rental);
        Page<Rental> page = new PageImpl<>(rentals);
        RentalSearchParameters rentalSearchParameters =
                new RentalSearchParameters(null, null);

        when(specificationBuilder.build(rentalSearchParameters))
                .thenReturn(Specification.where(null));
        when(rentalRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);

        List<? extends RentalDto> result =
                rentalService.getAll(user, pageable, rentalSearchParameters);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rentalDto, result.getFirst());
    }

    @Test
    @DisplayName("Get rental by ID with valid values")
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
    @DisplayName("Get a rental with a non-existent ID")
    void getRentalById_NotFound_exception() {
        when(rentalRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> {
                    rentalService.getById(RENTAL_ID, user);
            });
        assertEquals("Can't find a rental by id: " + RENTAL_ID, exception.getMessage());
    }
}
