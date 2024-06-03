package ua.team3.carsharingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.team3.carsharingservice.dto.UserResponseDto;
import ua.team3.carsharingservice.dto.UserRoleUpdateDto;
import ua.team3.carsharingservice.dto.UserUpdateRequestDto;
import ua.team3.carsharingservice.model.Role;
import ua.team3.carsharingservice.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("role")
    public void updateUserRole(@Valid @RequestBody UserRoleUpdateDto updateDto) {
        userService.updateUserRole(updateDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/me")
    public UserResponseDto getMyProfile() {
        return userService.getCurrentUserProfile();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/me")
    public UserResponseDto updateMyProfile(
            @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto
    ) {
        return userService.updateCurrentUserProfile(userUpdateRequestDto);
    }
}
