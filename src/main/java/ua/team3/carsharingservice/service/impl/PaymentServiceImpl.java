package ua.team3.carsharingservice.service.impl;

import static ua.team3.carsharingservice.model.Payment.Status.PAID;
import static ua.team3.carsharingservice.model.Payment.Status.PENDING;
import static ua.team3.carsharingservice.util.StripeConst.CANCELING_MESSAGE;
import static ua.team3.carsharingservice.util.StripeConst.CANCEL_ENDPOINT;
import static ua.team3.carsharingservice.util.StripeConst.SESSION_ID_PARAM;
import static ua.team3.carsharingservice.util.StripeConst.STATUS_PAID;
import static ua.team3.carsharingservice.util.StripeConst.SUCCESS_ENDPOINT;
import static ua.team3.carsharingservice.util.StripeConst.SUCCESS_MESSAGE;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentDto;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentResponseUrlDto;
import ua.team3.carsharingservice.dto.stripe.session.SessionCreateDto;
import ua.team3.carsharingservice.exception.InvalidPaymentTypeException;
import ua.team3.carsharingservice.exception.StripeSessionException;
import ua.team3.carsharingservice.mapper.PaymentMapper;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.model.Payment.Type;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.repository.PaymentRepository;
import ua.team3.carsharingservice.repository.RentalRepository;
import ua.team3.carsharingservice.service.PaymentHandler;
import ua.team3.carsharingservice.service.PaymentService;
import ua.team3.carsharingservice.service.PaymentSystemService;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentSystemService paymentSystemService;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentHandlerFactory handlerFactory;

    @Override
    public PaymentResponseUrlDto createPaymentSession(SessionCreateDto createDto) {
        String type = createDto.getPaymentType().toUpperCase();
        PaymentHandler paymentHandler = handlerFactory.getHandler(type);
        if (paymentHandler == null) {
                throw new InvalidPaymentTypeException("Payment type "
                        + createDto.getPaymentType() + " doesn't exist");
        }
        Type paymentType = getPaymentType(type);
        Optional<Payment> optionalPayment = paymentRepository.findByRentalIdAndType(
                createDto.getRentalId(), paymentType);
        Rental rental = rentalRepository.findById(createDto.getRentalId()).orElseThrow(
                () -> new EntityNotFoundException("rental with id: "
                        + createDto.getRentalId() + " doesn't exist")
        );
        if (!paymentHandler.canMakePayment(rental, optionalPayment)) {
            throw new StripeSessionException("Can't create payment for rental with id "
                    + rental.getId());
        }
        Car car = rental.getCar();
        long rentalDays = paymentHandler.calculateDays(rental);
        BigDecimal amount = paymentHandler.calculateAmount(car.getDailyFee(), rentalDays);
        String successUrl = buildSuccessUrl();
        String cancelUrl = buildCancelUrl();
        Session session =
                paymentSystemService.createPaymentSession(car.getBrand(), amount, successUrl,
                        cancelUrl);
        formAndSavePayment(paymentType, rental, amount, session);
        return new PaymentResponseUrlDto(session.getUrl());
    }

    @Override
    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(

        );
        return paymentMapper.toDto(payment);
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
        return STATUS_PAID.equals(session.getPaymentStatus());
    }

    private void formAndSavePayment(Type paymentType,
                                    Rental rental,
                                    BigDecimal amountToPay,
                                    Session session) {
        Payment payment = new Payment();
        payment.setType(paymentType);
        payment.setRental(rental);
        payment.setAmount(amountToPay);
        payment.setStatus(PENDING);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        paymentRepository.save(payment);
    }

    private Type getPaymentType(String paymentType) {
        return Type.valueOf(paymentType);
    }

    private String buildSuccessUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(SUCCESS_ENDPOINT)
                .toUriString() + SESSION_ID_PARAM;
    }

    private String buildCancelUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(CANCEL_ENDPOINT)
                .toUriString();
    }
}
