package ua.team3.carsharingservice.service.impl;

import static ua.team3.carsharingservice.model.Payment.Status.EXPIRED;
import static ua.team3.carsharingservice.model.Payment.Status.PAID;
import static ua.team3.carsharingservice.model.Payment.Status.PENDING;
import static ua.team3.carsharingservice.model.Payment.Type.FINE;
import static ua.team3.carsharingservice.model.Payment.Type.PAYMENT;
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
import java.util.Arrays;
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
import ua.team3.carsharingservice.mapper.PaymentMapper;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.model.Payment.Type;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.model.User;
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
    public PaymentResponseUrlDto createPaymentSession(SessionCreateDto createDto, User user) {
        Rental rental = rentalRepository.findByIdAndUserId(createDto.getRentalId(), user.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("You don't have rental with id: "
                                + createDto.getRentalId())
        );
        Type paymentType = getPaymentTypeIfValid(createDto.getPaymentType());
        Optional<Payment> optionalPayment = paymentRepository.findByRentalIdAndType(
                createDto.getRentalId(), paymentType);
        if (optionalPayment.isEmpty()) {
            throw new EntityNotFoundException("Can't find " + createDto.getPaymentType()
                    + " payment for rental with id " + createDto.getRentalId());
        }
        Car car = rental.getCar();
        String successUrl = buildSuccessUrl();
        String cancelUrl = buildCancelUrl();
        Payment payment = optionalPayment.get();
        Session session =
                paymentSystemService.createPaymentSession(car.getBrand(),
                        payment.getAmount(),
                        successUrl,
                        cancelUrl);
        setSessionToPayment(payment, session);
        return new PaymentResponseUrlDto(session.getUrl());
    }

    @Override
    public List<PaymentDto> getPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findPaymentsByUserId(userId, pageable)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public void handlePaymentSuccess(String sessionId) {
        Payment payment = updatePaymentStatus(sessionId, PAID);
    }

    @Override
    public void handleFailed(String sessionId) {
        Payment payment = updatePaymentStatus(sessionId, EXPIRED);
    }

    @Override
    public String returnCancelMessage() {
        return CANCELING_MESSAGE;
    }

    @Override
    public String returnSuccessMessage() {
        return SUCCESS_MESSAGE;
    }

    @Override
    public List<PaymentDto> getAllPayments(User user, Pageable pageable) {
        return user.isAdmin() ? getAllPaymentsForAdmin(pageable) :
                        getAllPaymentsByUser(user, pageable);
    }

    @Override
    public boolean isPaymentStatusPaid(String sessionId) {
        Session session = paymentSystemService.getSession(sessionId);
        return STATUS_PAID.equals(session.getPaymentStatus());
    }

    @Override
    public void createPaymentForRental(Rental rental) {
        createPayment(PAYMENT, rental);
    }

    @Override
    public void createFinePaymentIfNeeded(Rental rental) {
        LocalDate actualReturnDate = rental.getActualReturnDate();
        LocalDate returnDate = rental.getReturnDate();
        if (actualReturnDate != null
                && returnDate.isBefore(actualReturnDate)) {
            createPayment(FINE, rental);
        }
    }

    private Payment updatePaymentStatus(String sessionId, Payment.Status status) {
        Session session = paymentSystemService.getSession(sessionId);
        Payment payment = paymentRepository.findBySessionId(session.getId()).orElseThrow(
                () -> new EntityNotFoundException("Can`t find payment with session id " + sessionId)
        );
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    private void createPayment(Type paymentType, Rental rental) {
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setType(paymentType);
        payment.setStatus(PENDING);
        PaymentHandler paymentHandler = handlerFactory.getHandler(paymentType.name());
        long rentalDays = paymentHandler.calculateDays(rental);
        BigDecimal amount =
                paymentHandler.calculateAmount(rental.getCar().getDailyFee(), rentalDays);
        payment.setAmount(amount);
        paymentRepository.save(payment);
    }

    private List<PaymentDto> getAllPaymentsForAdmin(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    private List<PaymentDto> getAllPaymentsByUser(User user, Pageable pageable) {
        return paymentRepository.findPaymentsByUserId(user.getId(), pageable)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    private void setSessionToPayment(Payment payment, Session session) {
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        paymentRepository.save(payment);
    }

    private Type getPaymentTypeIfValid(String paymentType) {
        return Arrays.stream(Type.values())
                .filter(type -> type.name().equalsIgnoreCase(paymentType))
                .findFirst()
                .orElseThrow(() -> new InvalidPaymentTypeException("Payment type "
                        + paymentType + " doesn't exist"));
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
