package ua.team3.carsharingservice.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.team3.carsharingservice.service.PaymentService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Endpoints for Stripe Webhook",
        description = "Endpoints for receiving POST request from Stripe about events")
@RequestMapping("/stripe")
public class StripeWebhookController {
    private final PaymentService paymentService;
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Operation(summary = "Endpoint for Stripe Webhook",
            description = "This endpoint is used by Stripe for receiving requests about events. "
            + "It is not intended to be called directly.")
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
        Session session =
                (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
        switch (event.getType()) {
            case "checkout.session.completed", "checkout.session.async_payment_succeeded":
                paymentService.handlePaymentSuccess(session.getId());
                break;
            case "checkout.session.expired", "checkout.session.async_payment_failed":
                paymentService.handleFailed(session.getId());
                break;
            default:
                break;
        }
        return ResponseEntity.ok().build();
    }
}
