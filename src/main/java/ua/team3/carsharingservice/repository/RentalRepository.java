package ua.team3.carsharingservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.team3.carsharingservice.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByUserId(Long userId);

    Optional<Rental> findByIdAndUserId(Long id, Long userId);
}
