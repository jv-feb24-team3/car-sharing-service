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
    private static final String SUCCESS_URL = "http://localhost:8080/payments/success";
    private static final String CANCEL_URL = "http://localhost:8080/payments/cancel";
    private final PaymentRepository paymentRepository;
    private final PaymentSystemService paymentSystemService;

    @Override
    public PaymentResponseDto createPaymentSession(Long rentalId) {
        //get rental from db
        BigDecimal amount = new BigDecimal(125);
        String sessionId = paymentSystemService.createPaymentSession(amount, SUCCESS_URL,
                CANCEL_URL).getId();
        String sessionUrl = paymentSystemService.getSessionUrl(sessionId);
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setSessionUrl(sessionUrl);
        return responseDto;
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find payment with id " + id)
        );
    }
}
