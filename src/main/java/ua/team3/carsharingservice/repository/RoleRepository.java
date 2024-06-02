package ua.team3.carsharingservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.team3.carsharingservice.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(Role.RoleName role);
}
