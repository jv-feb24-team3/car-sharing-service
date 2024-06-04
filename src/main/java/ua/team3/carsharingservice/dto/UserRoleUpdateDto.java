package ua.team3.carsharingservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ua.team3.carsharingservice.model.Role;

@Data
public class UserRoleUpdateDto {
    @NotNull
    private Long userId;
    @NotNull
    private Role.RoleName role;
}
