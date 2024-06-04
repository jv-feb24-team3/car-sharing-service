package ua.team3.carsharingservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.team3.carsharingservice.dto.UserRegistrationRequestDto;
import ua.team3.carsharingservice.dto.UserResponseDto;
import ua.team3.carsharingservice.dto.UserRoleUpdateDto;
import ua.team3.carsharingservice.dto.UserUpdateRequestDto;
import ua.team3.carsharingservice.exception.EntityNotFoundException;
import ua.team3.carsharingservice.exception.RegistrationException;
import ua.team3.carsharingservice.mapper.UserMapper;
import ua.team3.carsharingservice.model.Role;
import ua.team3.carsharingservice.model.User;
import ua.team3.carsharingservice.repository.RoleRepository;
import ua.team3.carsharingservice.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDto userResponseDto;
    private UserRegistrationRequestDto registrationRequestDto;
    private UserUpdateRequestDto updateRequestDto;
    private UserRoleUpdateDto roleUpdateDto;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("First");
        user.setLastName("Last");

        role = new Role();
        role.setId(1L);
        role.setRole(Role.RoleName.USER);

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setEmail("test@example.com");

        registrationRequestDto = new UserRegistrationRequestDto();
        registrationRequestDto.setEmail("test@example.com");
        registrationRequestDto.setPassword("password");

        updateRequestDto = new UserUpdateRequestDto();
        updateRequestDto.setFirstName("UpdatedFirst");
        updateRequestDto.setLastName("UpdatedLast");
        updateRequestDto.setPassword("newpassword");

        roleUpdateDto = new UserRoleUpdateDto();
        roleUpdateDto.setUserId(1L);
        roleUpdateDto.setRole(Role.RoleName.ADMIN);
    }

    @Test
    void testSaveUserSuccess() throws RegistrationException {
        when(userRepository.existsByEmail(registrationRequestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(registrationRequestDto)).thenReturn(user);
        when(passwordEncoder.encode(registrationRequestDto.getPassword()))
                .thenReturn("encodedPassword");
        when(roleRepository.findByRole(Role.RoleName.USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userResponseDto);

        UserResponseDto responseDto = userService.save(registrationRequestDto);

        assertNotNull(responseDto);
        assertEquals(userResponseDto.getEmail(), responseDto.getEmail());

        verify(userRepository).existsByEmail(registrationRequestDto.getEmail());
        verify(userMapper).toModel(registrationRequestDto);
        verify(passwordEncoder).encode(registrationRequestDto.getPassword());
        verify(roleRepository).findByRole(Role.RoleName.USER);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void testSaveUserThrowsRegistrationException() {
        when(userRepository.existsByEmail(registrationRequestDto.getEmail())).thenReturn(true);

        assertThrows(RegistrationException.class, () -> userService.save(registrationRequestDto));

        verify(userRepository).existsByEmail(registrationRequestDto.getEmail());
        verify(userMapper, never()).toModel(any());
        verify(passwordEncoder, never()).encode(any());
        verify(roleRepository, never()).findByRole(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUserRoleSuccess() {
        when(userRepository.findById(roleUpdateDto.getUserId())).thenReturn(Optional.of(user));
        when(roleRepository.findByRole(roleUpdateDto.getRole())).thenReturn(Optional.of(role));

        userService.updateUserRole(roleUpdateDto);

        verify(userRepository).findById(roleUpdateDto.getUserId());
        verify(roleRepository).findByRole(roleUpdateDto.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserRoleThrowsException() {
        when(userRepository.findById(roleUpdateDto.getUserId()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUserRole(roleUpdateDto));

        verify(userRepository).findById(roleUpdateDto.getUserId());
        verify(roleRepository, never()).findByRole(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUserByEmailSuccess() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto responseDto = userService.getUserByEmail(user.getEmail());

        assertNotNull(responseDto);
        assertEquals(userResponseDto.getEmail(), responseDto.getEmail());

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper).toDto(user);
    }

    @Test
    void testGetUserByEmailThrowsException() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserByEmail(user.getEmail()));

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testUpdateUserProfileSuccess() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(updateRequestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userResponseDto);

        UserResponseDto responseDto = userService.updateUserProfile(
                user.getEmail(), updateRequestDto);

        assertNotNull(responseDto);
        assertEquals(userResponseDto.getEmail(), responseDto.getEmail());

        verify(userRepository).findByEmail(user.getEmail());
        verify(passwordEncoder).encode(updateRequestDto.getPassword());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(any(User.class));
    }

    @Test
    void testUpdateUserProfileThrowsException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserProfile(user.getEmail(), updateRequestDto));

        verify(userRepository).findByEmail(user.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void testGetCurrentUserProfileSuccess() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder>
                     mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(user.getEmail());
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(userMapper.toDto(user)).thenReturn(userResponseDto);

            UserResponseDto responseDto = userService.getCurrentUserProfile();

            assertNotNull(responseDto);
            assertEquals(userResponseDto.getEmail(), responseDto.getEmail());

            verify(authentication).getName();
            verify(userRepository).findByEmail(user.getEmail());
            verify(userMapper).toDto(user);
        }
    }

    @Test
    void testUpdateCurrentUserProfileSuccess() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        try (MockedStatic<SecurityContextHolder>
                     mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(user.getEmail());
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.encode(updateRequestDto.getPassword()))
                    .thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toDto(any(User.class))).thenReturn(userResponseDto);

            UserResponseDto responseDto = userService.updateCurrentUserProfile(updateRequestDto);

            assertNotNull(responseDto);
            assertEquals(userResponseDto.getEmail(), responseDto.getEmail());

            verify(authentication).getName();
            verify(userRepository).findByEmail(user.getEmail());
            verify(passwordEncoder).encode(updateRequestDto.getPassword());
            verify(userRepository).save(any(User.class));
            verify(userMapper).toDto(any(User.class));
        }
    }
}
