package ua.team3.carsharingservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.team3.carsharingservice.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRentalId(Long rentalId);

    Optional<Payment> findBySessionId(String sessionId);
}
