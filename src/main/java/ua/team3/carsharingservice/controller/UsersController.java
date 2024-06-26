package ua.team3.carsharingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.team3.carsharingservice.dto.UserResponseDto;
import ua.team3.carsharingservice.dto.UserRoleUpdateDto;
import ua.team3.carsharingservice.dto.UserUpdateRequestDto;
import ua.team3.carsharingservice.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations related to user management")
public class UsersController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/role")
    @Operation(summary = "Update user role",
            description = "Allows an admin to update the role of a user")
    public void updateUserRole(
            @Parameter(description = "User role update request")
            @Valid @RequestBody UserRoleUpdateDto updateDto) {
        userService.updateUserRole(updateDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/me")
    @Operation(summary = "Get current user's profile",
            description = "Retrieve the profile information of the currently authenticated user")
    public UserResponseDto getMyProfile() {
        return userService.getCurrentUserProfile();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/me")
    @Operation(summary = "Update current user's profile",
            description = "Update the profile information of the currently authenticated user")
    public UserResponseDto updateMyProfile(
            @Parameter(description = "User profile update request")
            @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto
    ) {
        return userService.updateCurrentUserProfile(userUpdateRequestDto);
    }
}
