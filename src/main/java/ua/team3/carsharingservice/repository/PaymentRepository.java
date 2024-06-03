package ua.team3.carsharingservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentDto;
import ua.team3.carsharingservice.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRentalIdAndType(Long rentalId, Payment.Type type);

    Optional<Payment> findBySessionId(String sessionId);

    List<PaymentDto> findAllBy();

    @Query("SELECT p FROM Payment p " +
            "JOIN FETCH p.rental r " +
            "JOIN FETCH r.user u WHERE u.id = :userId")
    List<Payment> findPaymentsByUserId(@Param("userId") Long userId, Pageable pageable);
}
