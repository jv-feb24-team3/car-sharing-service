package ua.team3.carsharingservice.service;


import com.stripe.model.checkout.Session;
import java.math.BigDecimal;

public interface PaymentSystemService {
    Session createPaymentSession(BigDecimal amount, String successUrl, String cancelUrl);

    String getSessionUrl(String sessionId);
}
