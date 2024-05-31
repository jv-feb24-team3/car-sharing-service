package ua.team3.carsharingservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
