package ua.team3.carsharingservice.service;


import com.stripe.model.checkout.Session;
import java.math.BigDecimal;

public interface PaymentSystemService {
    Session createPaymentSession(String productName, BigDecimal amount, String successUrl, String cancelUrl);

    Session getSession(String sessionId);
}
