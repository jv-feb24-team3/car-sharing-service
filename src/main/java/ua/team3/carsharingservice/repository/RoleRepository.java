package ua.team3.carsharingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.team3.carsharingservice.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
