package ua.team3.carsharingservice.service;


import com.stripe.model.checkout.Session;
import java.util.Map;

public interface PaymentService {
    Session createPaymentSession(long amount, String currency, String successUrl, String cancelUrl);

    Map<String, Object> getPaymentDetails(String userId);
}
