package ua.team3.carsharingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.dto.payment.PaymentResponseDto;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.repository.PaymentRepository;
import ua.team3.carsharingservice.service.PaymentService;
import ua.team3.carsharingservice.service.PaymentSystemService;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal CONVERSION_RATE = BigDecimal.valueOf(100);
    private static final String SUCCESS_URL = "http://localhost:8080/api/payments/success";
    private static final String CANCEL_URL = "http://localhost:8080/api/payments/cancel";
    private final PaymentRepository paymentRepository;
    private final PaymentSystemService paymentSystemService;

    @Override
    public PaymentResponseDto createPaymentSession(Long rentalId) {
        BigDecimal amount = new BigDecimal(125);
        BigDecimal amountToPay = amount.multiply(CONVERSION_RATE);
        String productName = "Audi A8";
        String sessionId = paymentSystemService.createPaymentSession(productName, amountToPay, SUCCESS_URL,
                CANCEL_URL).getId();
        String sessionUrl = paymentSystemService.getSessionUrl(sessionId);
        return new PaymentResponseDto(sessionUrl);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find payment with id " + id)
        );
    }
}
