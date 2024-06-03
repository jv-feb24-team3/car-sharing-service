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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
        if (!paymentHandler.canCreateSession(rental, optionalPayment.get())) {
            throw new StripeSessionException("Can't create session for payment with rental id "
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
        setSessionToPayment(optionalPayment.get(), amount, session);
        return new PaymentResponseUrlDto(session.getUrl());
    }

    @Override
    public List<PaymentDto> getPaymentsByUserId(Long userId, Pageable pageable) {
        List<Payment> paymentList = paymentRepository.findPaymentsByUserId(userId, pageable);
        if (paymentList.isEmpty()) {
            throw new EntityNotFoundException("User with id " + userId + "payment history is empty");
        }
        return paymentList.stream()
                .map(paymentMapper::toDto)
                .toList();
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

    @Override
    public void createPaymentForRental(Rental rental) {
        createPayment(Type.PAYMENT, rental);
    }

    @Override
    public void createFinePaymentIfNeeded(Rental rental) {
        LocalDate actualReturnDate = rental.getActualReturnDate();
        LocalDate returnDate = rental.getReturnDate();
        if (actualReturnDate != null
                && returnDate.isBefore(actualReturnDate)) {
            createPayment(Type.FINE, rental);
        }
    }

    private void createPayment(Type paymentType, Rental rental) {
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setType(paymentType);
        payment.setStatus(PENDING);
        paymentRepository.save(payment);
    }

    private void setSessionToPayment(Payment payment,
                                     BigDecimal amountToPay,
                                     Session session) {
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setAmount(amountToPay);
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
