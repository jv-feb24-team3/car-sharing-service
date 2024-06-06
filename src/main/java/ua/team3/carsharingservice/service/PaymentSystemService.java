package ua.team3.carsharingservice.service;

import com.stripe.model.checkout.Session;
import ua.team3.carsharingservice.model.Payment;

public interface PaymentSystemService {
    Session createPaymentSession(Payment payment,
                                 String successUrl,
                                 String cancelUrl);

    Session getSession(String sessionId);
}
