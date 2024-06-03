package ua.team3.carsharingservice.service;

import ua.team3.carsharingservice.dto.UserRegistrationRequestDto;
import ua.team3.carsharingservice.dto.UserResponseDto;
import ua.team3.carsharingservice.dto.UserRoleUpdateDto;
import ua.team3.carsharingservice.dto.UserUpdateRequestDto;
import ua.team3.carsharingservice.exception.RegistrationException;

public interface UserService {
    UserResponseDto save(UserRegistrationRequestDto requestDto) throws RegistrationException;

    void updateUserRole(UserRoleUpdateDto updateDto);

    UserResponseDto getUserByEmail(String email);

    UserResponseDto updateUserProfile(String email, UserUpdateRequestDto userUpdateRequestDto);

    UserResponseDto getCurrentUserProfile();

    UserResponseDto updateCurrentUserProfile(UserUpdateRequestDto userUpdateRequestDto);
}
