package ua.team3.carsharingservice.service.impl;

import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.dto.UserRegistrationRequestDto;
import ua.team3.carsharingservice.dto.UserResponseDto;
import ua.team3.carsharingservice.dto.UserUpdateRequestDto;
import ua.team3.carsharingservice.exception.RegistrationException;
import ua.team3.carsharingservice.mapper.UserMapper;
import ua.team3.carsharingservice.model.Role;
import ua.team3.carsharingservice.model.User;
import ua.team3.carsharingservice.repository.RoleRepository;
import ua.team3.carsharingservice.repository.UserRepository;
import ua.team3.carsharingservice.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto save(UserRegistrationRequestDto requestDto
    ) throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email: "
                    + requestDto.getEmail() + " already exist!");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Optional<Role> optionalRole = roleRepository.findByRole(Role.RoleName.USER);
        Role defaultRole = optionalRole.orElseThrow(
                () -> new IllegalArgumentException("Default role not found!")
        );
        user.setRoles(Collections.singleton(defaultRole));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public void updateUserRole(Long userId, Role.RoleName role) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found!")
        );
        Role newRole = roleRepository.findByRole(role).orElseThrow(
                () -> new IllegalArgumentException("Role not found!")
        );
        user.setRoles(Collections.singleton(newRole));
        userRepository.save(user);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("User not found!")
        );
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateUserProfile(
            String email,
            UserUpdateRequestDto userUpdateRequestDto
    ) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("User not found!")
        );
        user.setFirstName(userUpdateRequestDto.getFirstName());
        user.setLastName(userUpdateRequestDto.getLastName());
        user.setPassword(passwordEncoder.encode(userUpdateRequestDto.getPassword()));

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserResponseDto getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return getUserByEmail(email);
    }

    @Override
    public UserResponseDto updateCurrentUserProfile(UserUpdateRequestDto userUpdateRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return updateUserProfile(email, userUpdateRequestDto);
    }
}
