package ua.team3.carsharingservice.service;

import static ua.team3.carsharingservice.model.Payment.Type;

import ua.team3.carsharingservice.dto.stripe.payment.PaymentDto;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentResponseUrlDto;
import ua.team3.carsharingservice.dto.stripe.session.SessionCreateDto;
import ua.team3.carsharingservice.model.Rental;

public interface PaymentService {
    PaymentResponseUrlDto createPaymentSession(SessionCreateDto createDto);

    PaymentDto getPaymentById(Long id);

    String handlePaymentSuccess(String sessionId);

    String handlePaymentCanceling();

    boolean isPaymentStatusPaid(String sessionId);

    void createPayment(Type paymentType, Rental rental);
}
