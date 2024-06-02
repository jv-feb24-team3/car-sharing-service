package ua.team3.carsharingservice.mapper;

import org.mapstruct.Mapper;
import ua.team3.carsharingservice.config.MapperConfig;
import ua.team3.carsharingservice.dto.CarDto;
import ua.team3.carsharingservice.dto.CarWithoutInventoryDto;
import ua.team3.carsharingservice.dto.CreateCarRequestDto;
import ua.team3.carsharingservice.model.Car;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toModel(CreateCarRequestDto requestDto);

    CarWithoutInventoryDto toDtoWithoutInventory(Car car);
}
