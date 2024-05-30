package ua.team3.carsharingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.dto.CarDto;
import ua.team3.carsharingservice.dto.CreateCarRequestDto;
import ua.team3.carsharingservice.mapper.CarMapper;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.repository.CarRepository;
import ua.team3.carsharingservice.service.CarService;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto addCar(CreateCarRequestDto requestDto) {
        Car car = carMapper.toModel(requestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public List<CarDto> findAllCars(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDto findCartById(Long id) {
        return carRepository.findById(id)
                .map(carMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car with id: " + id));
    }

    @Override
    public CarDto updateById(Long id, CreateCarRequestDto requestDto) {
        Car car = carRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't find car with id: " + id));
        car.setBrand(requestDto.getBrand());
        car.setType(requestDto.getType());
        car.setInventory(requestDto.getInventory());
        car.setDailyFee(requestDto.getDailyFee());
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public void deleteById(Long id) {
        carRepository.findById(id).ifPresentOrElse(carRepository::delete, () -> {
            throw new EntityNotFoundException("Can't find car with id: " + id);
        });
    }
}
