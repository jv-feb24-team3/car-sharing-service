package ua.team3.carsharingservice.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ua.team3.carsharingservice.dto.CarDto;
import ua.team3.carsharingservice.dto.CreateCarRequestDto;

public interface CarService {
    CarDto addCar(CreateCarRequestDto requestDto);

    List<CarDto> findAllCars(Pageable pageable);

    CarDto findCarById(Long id);

    CarDto updateById(Long id, CreateCarRequestDto requestDto);

    void deleteById(Long id);
}
