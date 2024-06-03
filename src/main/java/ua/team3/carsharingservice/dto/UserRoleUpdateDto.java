package ua.team3.carsharingservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ua.team3.carsharingservice.model.Role;

@Data
public class UserRoleUpdateDto {
    @NotBlank
    private Long userId;
    @NotBlank
    private Role.RoleName role;
}
