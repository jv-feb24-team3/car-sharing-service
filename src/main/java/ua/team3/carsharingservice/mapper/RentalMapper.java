package ua.team3.carsharingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ua.team3.carsharingservice.config.MapperConfig;
import ua.team3.carsharingservice.dto.RentalDto;
import ua.team3.carsharingservice.dto.RentalForAdminDto;
import ua.team3.carsharingservice.dto.RentalRequestDto;
import ua.team3.carsharingservice.model.Rental;

@Mapper(config = MapperConfig.class, uses = {CarMapper.class})
public interface RentalMapper {
    RentalDto toDto(Rental model);

    @Mappings({
            @Mapping(source = "user.id", target = "userId")
    })
    RentalForAdminDto toDtoForAdmin(Rental model);

    @Mappings({
            @Mapping(source = "carId", target = "car.id"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", ignore = true),
            @Mapping(target = "actualReturnDate", ignore = true)
    })
    Rental toModel(RentalRequestDto dto);
}
