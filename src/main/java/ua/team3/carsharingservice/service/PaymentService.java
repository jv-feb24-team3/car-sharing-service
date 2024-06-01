package ua.team3.carsharingservice.service;

import ua.team3.carsharingservice.dto.stripe.session.SessionCreateDto;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentResponseDto;
import ua.team3.carsharingservice.model.Payment;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(SessionCreateDto createDto);

    Payment getPaymentById(Long id);

    String handlePaymentSuccess(String sessionId);

    String handlePaymentCanceling();

    boolean isPaymentStatusPaid(String sessionId);
}
