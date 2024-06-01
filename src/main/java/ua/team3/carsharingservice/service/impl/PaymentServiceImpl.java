package ua.team3.carsharingservice.service.impl;

import static ua.team3.carsharingservice.model.Payment.Status.EXPIRED;
import static ua.team3.carsharingservice.model.Payment.Status.PAID;
import static ua.team3.carsharingservice.model.Payment.Status.PENDING;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentResponseDto;
import ua.team3.carsharingservice.dto.stripe.session.SessionCreateDto;
import ua.team3.carsharingservice.exception.InvalidPaymentTypeException;
import ua.team3.carsharingservice.exception.PaymentProcessedException;
import ua.team3.carsharingservice.exception.StripeSessionException;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.model.Payment.Type;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.repository.PaymentRepository;
import ua.team3.carsharingservice.repository.RentalRepository;
import ua.team3.carsharingservice.service.PaymentService;
import ua.team3.carsharingservice.service.PaymentSystemService;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String SUCCESS_URL =
            "http://localhost:8080/api/payments/success?session_id={CHECKOUT_SESSION_ID}";
    private static final String CANCEL_URL =
            "http://localhost:8080/api/payments/cancel";
    private static final String SUCCESS_MESSAGE =
            "Your payment was successful! Your car rental is confirmed.";
    private static final String CANCELING_MESSAGE =
            "Your payment was canceled. You can complete your car rental within the next 24 hours.";
    private static final String PAID_STATUS = "paid";
    private final PaymentRepository paymentRepository;
    private final PaymentSystemService paymentSystemService;
    private final RentalRepository rentalRepository;

    @Override
    public PaymentResponseDto createPaymentSession(SessionCreateDto createDto) {
        Optional<Payment> optionalPayment =
                paymentRepository.findByRentalId(createDto.getRentalId());
        if (optionalPayment.isPresent() && !optionalPayment.get().getStatus().equals(EXPIRED)) {
            if (optionalPayment.get().getStatus().equals(PAID)) {
                throw new PaymentProcessedException("The rent has already been paid");
            }
            throw new StripeSessionException("Payment session is already exist");
        }
        Rental rental = rentalRepository.findById(createDto.getRentalId()).get();
        Car car = rental.getCar();
        long rentalDays = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        BigDecimal amount = car.getDailyFee().multiply(BigDecimal.valueOf(rentalDays));
        String productName = car.getBrand();
        Session session =
                paymentSystemService.createPaymentSession(productName, amount, SUCCESS_URL,
                        CANCEL_URL);
        formAndSavePayment(createDto.getPaymentType(), rental, amount, session);
        return new PaymentResponseDto(session.getUrl());
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can`t find payment with id " + id)
        );
    }

    @Override
    public String handlePaymentSuccess(String sessionId) {
        Session session = paymentSystemService.getSession(sessionId);
        Payment payment = paymentRepository.findBySessionId(session.getId()).orElseThrow(
                () -> new EntityNotFoundException("Can`t find payment with session id " + sessionId)
        );
        payment.setStatus(PAID);
        paymentRepository.save(payment);
        return SUCCESS_MESSAGE;
    }

    @Override
    public String handlePaymentCanceling() {
        return CANCELING_MESSAGE;
    }

    @Override
    public boolean isPaymentStatusPaid(String sessionId) {
        Session session = paymentSystemService.getSession(sessionId);
        return PAID_STATUS.equals(session.getPaymentStatus());
    }

    private void formAndSavePayment(String paymentType,
                                    Rental rental,
                                    BigDecimal amountToPay,
                                    Session session) {
        Payment payment = new Payment();
        Type type = getPaymentTypeIfValid(paymentType);
        payment.setType(type);
        payment.setRental(rental);
        payment.setAmount(amountToPay);
        payment.setStatus(PENDING);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        paymentRepository.save(payment);
    }

    private Type getPaymentTypeIfValid(String paymentType) {
        return Arrays.stream(Type.values())
                .filter(type -> type.name().equals(paymentType.toUpperCase()))
                .findFirst()
                .orElseThrow(
                        () -> new InvalidPaymentTypeException("Payment type "
                                + paymentType + " doesn't exist")
                );
    }
}
