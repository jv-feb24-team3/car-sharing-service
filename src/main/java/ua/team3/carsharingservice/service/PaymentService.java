package ua.team3.carsharingservice.service;

import ua.team3.carsharingservice.dto.payment.PaymentResponseDto;
import ua.team3.carsharingservice.model.Payment;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(Long rentalId);

    Payment getPaymentById(Long id);
}
