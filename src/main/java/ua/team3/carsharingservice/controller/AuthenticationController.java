package ua.team3.carsharingservice.controller;

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
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public UserResponseDto register(
            @Valid @RequestBody UserRegistrationRequestDto requestDto
    ) throws RegistrationException {
        return userService.save(requestDto);
    }

    @PostMapping("/login")
    public UserLoginResponseDto login(
            @Valid @RequestBody UserLoginRequestDto requestDto
    ) {
        return authenticationService.authenticate(requestDto);
    }
}
