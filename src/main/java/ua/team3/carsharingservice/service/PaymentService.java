package ua.team3.carsharingservice.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentDto;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentResponseUrlDto;
import ua.team3.carsharingservice.dto.stripe.session.SessionCreateDto;
import ua.team3.carsharingservice.model.Rental;

public interface PaymentService {
    PaymentResponseUrlDto createPaymentSession(SessionCreateDto createDto);

    List<PaymentDto> getPaymentsByUserId(Long userId, Pageable pageable);

    String handlePaymentSuccess(String sessionId);

    String handlePaymentCanceling();

    boolean isPaymentStatusPaid(String sessionId);

    void createPaymentForRental(Rental rental);

    void createFinePaymentIfNeeded(Rental rental);
}
