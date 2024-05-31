package ua.team3.carsharingservice.service.impl;

import static com.stripe.param.checkout.SessionCreateParams.LineItem;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
import static com.stripe.param.checkout.SessionCreateParams.Mode;
import static com.stripe.param.checkout.SessionCreateParams.PaymentMethodType;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.dto.payment.PaymentDto;
import ua.team3.carsharingservice.exception.StripeSessionException;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.repository.PaymentRepository;
import ua.team3.carsharingservice.service.PaymentService;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String PRODUCT_NAME = "Car Rental Payment";
    private static final Long DEFAULT_QUANTITY = 1L;
    private final PaymentRepository paymentRepository;

    public Session createPaymentSession(long amount, String currency, String successUrl,
                                        String cancelUrl) {
        SessionCreateParams params = buildSessionParams(amount, currency, successUrl, cancelUrl);

        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new StripeSessionException("Can`t create session", e);
        }
    }


    public Payment getPaymentDetails(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    private SessionCreateParams buildSessionParams(long amount,
                                                   String currency,
                                                   String successUrl,
                                                   String cancelUrl) {
        return SessionCreateParams.builder()
                .addPaymentMethodType(PaymentMethodType.CARD)
                .setMode(Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(buildLineItem(amount, currency)).build();
    }

    private LineItem buildLineItem(long amount, String currency) {
        return LineItem.builder()
                .setQuantity(DEFAULT_QUANTITY)
                .setPriceData(buildPriceData(amount, currency)).build();
    }

    private PriceData buildPriceData(long amount, String currency) {
        return PriceData.builder()
                .setCurrency(currency)
                .setUnitAmount(amount)
                .setProductData(buildProductData()).build();
    }

    private ProductData buildProductData() {
        return ProductData.builder()
                .setName(PRODUCT_NAME)
                .build();
    }
}
