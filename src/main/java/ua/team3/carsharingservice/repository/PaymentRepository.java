package ua.team3.carsharingservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.team3.carsharingservice.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRentalIdAndType(Long rentalId, Payment.Type type);

    Optional<Payment> findBySessionId(String sessionId);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' "
            + "AND p.createdAt <= :timeLimit "
            + "AND p.sessionId IS NULL")
    List<Payment> findPendingPaymentsOlderThan(@Param("timeLimit") LocalDateTime timeLimit);

    @Query("SELECT p FROM Payment p "
            + "JOIN FETCH p.rental r "
            + "JOIN FETCH r.user u WHERE u.id = :userId")
    List<Payment> findPaymentsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Payment p " +
            "JOIN FETCH p.rental r " +
            "JOIN FETCH r.user " +
            "WHERE p.id = :id")
    Optional<Payment> findByIdAndFetchDetailsEagerly(@Param("id") Long id);
}
