package ua.team3.carsharingservice.controller;

import com.stripe.model.checkout.Session;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.model.User;
import ua.team3.carsharingservice.service.PaymentService;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    public List<Payment> getPayments(Authentication authentication) {
        User user = (User) authentication.get
        return paymentService.getPaymentDetails();
    }

    @PostMapping
    public void createPaymentSession(@RequestParam int amount,
                                     @RequestParam String currency,
                                     HttpServletResponse response)
            throws IOException {
        String successUrl = "http://localhost:8080/payments/success";
        String cancelUrl = "http://localhost:8080/payments/cancel";
        Session session =
                paymentService.createPaymentSession(amount, currency, successUrl, cancelUrl);
        response.sendRedirect(session.getUrl());
    }

    @GetMapping("/success")
    public String paymentSuccess() {
        return "Payment was successful!";
    }

    @GetMapping("/cancel")
    public String paymentCancel() {
        return "Payment was canceled!";
    }

}
