package ua.team3.carsharingservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.team3.carsharingservice.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Override
    @EntityGraph(attributePaths = {"car", "user"})
    Page<Rental> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"car"})
    List<Rental> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"car"})
    List<Rental> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"car"})
    Optional<Rental> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"car"})
    Optional<Rental> findById(Long id);

    @Query(value = "SELECT * FROM rentals r "
            + "WHERE r.return_date <= DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY) "
            + "AND r.actual_return_date IS NULL AND status NOT LIKE 'CANCELLED'",
               nativeQuery = true)
    List<Rental> getOverdueRentals();
}
