package ua.team3.carsharingservice.repository;

import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.team3.carsharingservice.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByUserId(Long userId);
}
