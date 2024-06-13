package ua.team3.carsharingservice.service.impl.payment;

import static com.stripe.param.checkout.SessionCreateParams.LineItem;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import static com.stripe.param.checkout.SessionCreateParams.Mode;
import static com.stripe.param.checkout.SessionCreateParams.PaymentMethodType;
import static ua.team3.carsharingservice.util.StripeConst.CONVERSATION_RATE;
import static ua.team3.carsharingservice.util.StripeConst.CURRENCY;
import static ua.team3.carsharingservice.util.StripeConst.DEFAULT_QUANTITY;
import static ua.team3.carsharingservice.util.StripeConst.MIN_SESSION_LIFETIME_IN_HOURS;
import static ua.team3.carsharingservice.util.StripeConst.SESSION_DURATION;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.exception.StripeSessionException;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.service.PaymentSystemService;

@Service
@RequiredArgsConstructor
public class StripeService implements PaymentSystemService {
    public Session createPaymentSession(Payment payment,
                                        String successUrl,
                                        String cancelUrl) {
        SessionCreateParams params =
                buildSessionParams(payment, successUrl, cancelUrl);
        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new StripeSessionException("Can`t create session", e);
        }
    }

    @Override
    public Session getSession(String sessionId) {
        Session session;
        try {
            session = Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new StripeSessionException("Can't retrieve session with id" + sessionId, e);
        }
        return session;
    }

    private SessionCreateParams buildSessionParams(Payment payment,
                                                   String successUrl,
                                                   String cancelUrl) {

        long remainingSessionLifetime =
                calculateRemainingSessionLifetime(payment.getCreatedAt());
        return SessionCreateParams.builder()
                .addPaymentMethodType(PaymentMethodType.CARD)
                .setMode(Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setExpiresAt(Instant.now()
                        .plus(remainingSessionLifetime, ChronoUnit.HOURS).getEpochSecond())
                .addLineItem(buildLineItem(payment)).build();
    }

    private LineItem buildLineItem(Payment payment) {
        return LineItem.builder()
                .setQuantity(DEFAULT_QUANTITY)
                .setPriceData(buildPriceData(payment)).build();
    }

    private PriceData buildPriceData(Payment payment) {
        return PriceData.builder()
                .setCurrency(CURRENCY)
                .setUnitAmount(payment.getAmount().longValue() * CONVERSATION_RATE)
                .setProductData(buildProductData(payment)).build();
    }

    private ProductData buildProductData(Payment payment) {
        return ProductData.builder()
                .setName(payment.getBillingDetails())
                .build();
    }

    private long calculateRemainingSessionLifetime(LocalDateTime createdAt) {
        LocalDateTime currentTime = LocalDateTime.now();
        long sessionLifetimeInHours = Duration.between(createdAt, currentTime).toHours();
        long remainingLifetimeInHours = SESSION_DURATION - sessionLifetimeInHours;
        return Math.max(remainingLifetimeInHours, MIN_SESSION_LIFETIME_IN_HOURS);
    }
}
