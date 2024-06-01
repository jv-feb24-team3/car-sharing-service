package ua.team3.carsharingservice.service.impl;

import static com.stripe.param.checkout.SessionCreateParams.LineItem;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import static com.stripe.param.checkout.SessionCreateParams.Mode;
import static com.stripe.param.checkout.SessionCreateParams.PaymentMethodType;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.exception.StripeSessionException;
import ua.team3.carsharingservice.service.PaymentSystemService;

@Service
@RequiredArgsConstructor
public class StripeService implements PaymentSystemService {
    private static final Long CONVERSATION_RATE = 100L;
    private static final Long DEFAULT_QUANTITY = 1L;
    private static final String CURRENCY = "USD";

    public Session createPaymentSession(String productName,
                                        BigDecimal amount,
                                        String successUrl,
                                        String cancelUrl) {
        SessionCreateParams params =
                buildSessionParams(productName, amount, successUrl, cancelUrl);
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
            throw new StripeSessionException("Can't retrieve session", e);
        }
        return session;
    }

    private SessionCreateParams buildSessionParams(String productName,
                                                   BigDecimal amount,
                                                   String successUrl,
                                                   String cancelUrl) {
        long expiresAt = Instant.now().plus(24, ChronoUnit.HOURS).getEpochSecond();
        return SessionCreateParams.builder()
                .addPaymentMethodType(PaymentMethodType.CARD)
                .setMode(Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .setExpiresAt(expiresAt)
                .addLineItem(buildLineItem(productName, amount)).build();
    }

    private LineItem buildLineItem(String productName, BigDecimal amount) {
        return LineItem.builder()
                .setQuantity(DEFAULT_QUANTITY)
                .setPriceData(buildPriceData(productName, amount)).build();
    }

    private PriceData buildPriceData(String productName, BigDecimal amount) {
        return PriceData.builder()
                .setCurrency(CURRENCY)
                .setUnitAmount(amount.longValue() * CONVERSATION_RATE)
                .setProductData(buildProductData(productName)).build();
    }

    private ProductData buildProductData(String productName) {
        return ProductData.builder()
                .setName(productName)
                .build();
    }
}
