package ua.team3.carsharingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.team3.carsharingservice.dto.UserLoginRequestDto;
import ua.team3.carsharingservice.dto.UserLoginResponseDto;
import ua.team3.carsharingservice.dto.UserRegistrationRequestDto;
import ua.team3.carsharingservice.dto.UserResponseDto;
import ua.team3.carsharingservice.exception.RegistrationException;
import ua.team3.carsharingservice.security.AuthenticationService;
import ua.team3.carsharingservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Tag(name = "Authentication",
        description = "Operations related to user authentication and registration")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Register a new user",
            description = "Register a new user by providing registration details")
    public UserResponseDto register(
            @Parameter(description = "User registration request details")
            @Valid @RequestBody UserRegistrationRequestDto requestDto
    ) throws RegistrationException {
        return userService.save(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user",
            description = "Authenticate a user by providing login details")
    public UserLoginResponseDto login(
            @Parameter(description = "User login request details")
            @Valid @RequestBody UserLoginRequestDto requestDto
    ) {
        return authenticationService.authenticate(requestDto);
    }
}
