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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.exception.StripeSessionException;
import ua.team3.carsharingservice.service.PaymentSystemService;

@Service
@RequiredArgsConstructor
public class StripeService implements PaymentSystemService {
    private static final Long DEFAULT_QUANTITY = 1L;
    private static final String CURRENCY = "USD";

    public Session createPaymentSession(String productName, BigDecimal amount, String successUrl,
                                        String cancelUrl) {
        SessionCreateParams params = buildSessionParams(productName, amount, successUrl, cancelUrl);

        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new StripeSessionException("Can`t create session", e);
        }
    }

    @Override
    public String getSessionUrl(String sessionId) {
        Session session = null;
        try {
            session = Session.retrieve(sessionId);
        } catch (StripeException e) {
            throw new StripeSessionException("Can't retrieve session", e);
        }
        return session.getUrl();
    }

    private SessionCreateParams buildSessionParams(String productName,
                                                   BigDecimal amount,
                                                   String successUrl,
                                                   String cancelUrl) {
        return SessionCreateParams.builder()
                .addPaymentMethodType(PaymentMethodType.CARD)
                .setMode(Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
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
                .setUnitAmount(amount.longValue())
                .setProductData(buildProductData(productName)).build();
    }

    private ProductData buildProductData(String productName) {
        return ProductData.builder()
                .setName(productName)
                .build();
    }
}
