package ua.team3.carsharingservice.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ua.team3.carsharingservice.dto.CarDto;
import ua.team3.carsharingservice.dto.CreateCarRequestDto;
import ua.team3.carsharingservice.mapper.CarMapper;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.repository.CarRepository;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    private static final String BMW_BRAND = "BMW";
    private static final Long CAR_ID = 1L;
    private static final String TESLA_BRAND = "Tesla";
    private static final Long SECOND_CAR_ID = 2L;
    private static final String UPDATED_CAR_BRAND = "Audi";
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Add new car with valid values")
    void addNewCar_validData_success() {
        CreateCarRequestDto carRequestDto = new CreateCarRequestDto();
        carRequestDto.setDailyFee(BigDecimal.valueOf(100));
        carRequestDto.setType(Car.CarType.SEDAN);
        carRequestDto.setBrand(BMW_BRAND);
        carRequestDto.setInventory(10);

        CarDto carDto = new CarDto();
        carDto.setId(CAR_ID);
        carDto.setDailyFee(carRequestDto.getDailyFee());
        carDto.setBrand(carRequestDto.getBrand());
        carDto.setInventory(carRequestDto.getInventory());
        carDto.setType(carRequestDto.getType());

        Car car = new Car();
        when(carMapper.toModel(carRequestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto createdCar = carService.addCar(carRequestDto);

        assertNotNull(createdCar);
        assertEquals(BMW_BRAND, createdCar.getBrand());
        assertEquals(10, createdCar.getInventory());
        assertEquals(Car.CarType.SEDAN, createdCar.getType());
    }

    @Test
    @DisplayName("Return list of cars")
    void findAllCars_validData_success() {
        Car firstCar = new Car();
        firstCar.setId(CAR_ID);
        firstCar.setBrand(BMW_BRAND);

        Car secondCar = new Car();
        secondCar.setId(SECOND_CAR_ID);
        secondCar.setBrand(TESLA_BRAND);

        CarDto firstCarDto = new CarDto();
        firstCarDto.setId(CAR_ID);
        firstCarDto.setBrand(BMW_BRAND);

        CarDto secondCarDto = new CarDto();
        secondCarDto.setId(SECOND_CAR_ID);
        secondCarDto.setBrand(TESLA_BRAND);

        List<Car> cars = List.of(firstCar, secondCar);
        Page<Car> carPage = new PageImpl<>(cars);
        Pageable pageable = Pageable.ofSize(1);

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(firstCar)).thenReturn(firstCarDto);
        when(carMapper.toDto(secondCar)).thenReturn(secondCarDto);
        
        List<CarDto> result = carService.findAllCars(pageable);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(CAR_ID, result.get(0).getId());
        assertEquals(BMW_BRAND, result.get(0).getBrand());
        assertEquals(SECOND_CAR_ID, result.get(1).getId());
        assertEquals(TESLA_BRAND, result.get(1).getBrand());
    }
    
    @Test
    @DisplayName("Return car by id")
    void findById_validData_success() {
        Car firstCar = new Car();
        firstCar.setId(CAR_ID);
        firstCar.setBrand(BMW_BRAND);

        Car secondCar = new Car();
        secondCar.setId(SECOND_CAR_ID);
        secondCar.setBrand(TESLA_BRAND);

        CarDto firstCarDto = new CarDto();
        firstCarDto.setId(CAR_ID);
        firstCarDto.setBrand(BMW_BRAND);

        CarDto secondCarDto = new CarDto();
        secondCarDto.setId(SECOND_CAR_ID);
        secondCarDto.setBrand(TESLA_BRAND);


        when(carRepository.findById(firstCar.getId())).thenReturn(Optional.of(firstCar));
        when(carRepository.findById(secondCar.getId())).thenReturn(Optional.of(secondCar));
        when(carMapper.toDto(firstCar)).thenReturn(firstCarDto);
        when(carMapper.toDto(secondCar)).thenReturn(secondCarDto);

        CarDto firstResult = carService.findCartById(firstCar.getId());
        CarDto secondResult = carService.findCartById(secondCar.getId());

        assertEquals(firstCar.getId(), firstResult.getId());
        assertEquals(firstCar.getBrand(), firstResult.getBrand());
        assertEquals(secondCarDto, secondResult);
        assertEquals(secondCar.getId(), secondResult.getId());
        assertEquals(secondCar.getBrand(), secondResult.getBrand());
    }

    @Test
    @DisplayName("Return car with wrong id")
    void findCarById_InvalidData_ExceptionMessage() {
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            carService.findCartById(CAR_ID);
        });

        assertEquals("Can't find car with id: " + CAR_ID, thrown.getMessage());
    }

    @Test
    @DisplayName("Update car by id")
    void updateCarById_validData_success() {
        CreateCarRequestDto carRequestDto = new CreateCarRequestDto();
        carRequestDto.setBrand(UPDATED_CAR_BRAND);

        CarDto carDto = new CarDto();
        carDto.setId(CAR_ID);
        carDto.setBrand(carRequestDto.getBrand());

        Car car = new Car();
        car.setId(CAR_ID);
        car.setBrand(BMW_BRAND);

        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto updatedCar = carService.updateById(CAR_ID, carRequestDto);

        assertEquals(UPDATED_CAR_BRAND, updatedCar.getBrand());
    }

    @Test
    @DisplayName("Return car with wrong id")
    void updateCarById_InvalidData_ExceptionMessage() {
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.empty());

        CreateCarRequestDto carRequestDto = new CreateCarRequestDto();
        carRequestDto.setBrand(UPDATED_CAR_BRAND);

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            carService.updateById(CAR_ID, carRequestDto);
        });

        assertEquals("Can't find car with id: " + CAR_ID, thrown.getMessage());
    }

    @Test
    @DisplayName("Delete car by id")
    void deleteCarById_validData_success() {
        Car car = new Car();
        car.setId(CAR_ID);
        car.setBrand(BMW_BRAND);

        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(car));

        carService.deleteById(CAR_ID);

        verify(carRepository, times(1)).delete(car);
    }

    @Test
    @DisplayName("Delete car with wrong id")
    void deleteCarById_InvalidData_ExceptionMessage() {
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            carService.deleteById(CAR_ID);
        });

        assertEquals("Can't find car with id: " + CAR_ID, thrown.getMessage());
    }
}
