package ua.team3.carsharingservice.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentDto;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentResponseUrlDto;
import ua.team3.carsharingservice.dto.stripe.session.SessionCreateDto;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.model.User;

public interface PaymentService {
    PaymentResponseUrlDto createPaymentSession(SessionCreateDto createDto, User user);

    List<PaymentDto> getPaymentsByUserId(Long userId, Pageable pageable);

    void handlePaymentSuccess(String sessionId);

    void handleFailed(String sessionId);

    String returnCancelMessage();

    String returnSuccessMessage();

    List<PaymentDto> getAllPayments(User user, Pageable pageable);

    boolean isPaymentStatusPaid(String sessionId);

    void createPaymentForRental(Rental rental);

    void createFinePaymentIfNeeded(Rental rental);

    void updateExpiredPayments();
}
