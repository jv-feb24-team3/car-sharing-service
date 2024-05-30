package ua.team3.carsharingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.team3.carsharingservice.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
