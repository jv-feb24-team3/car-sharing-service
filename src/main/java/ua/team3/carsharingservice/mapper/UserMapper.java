package ua.team3.carsharingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.team3.carsharingservice.config.MapperConfig;
import ua.team3.carsharingservice.dto.UserRegistrationRequestDto;
import ua.team3.carsharingservice.dto.UserResponseDto;
import ua.team3.carsharingservice.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toModel(UserRegistrationRequestDto requestDto);
}
