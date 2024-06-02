package ua.team3.carsharingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentDto;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentResponseUrlDto;
import ua.team3.carsharingservice.dto.stripe.session.SessionCreateDto;
import ua.team3.carsharingservice.service.PaymentService;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment management", description = "Endpoints for payment management")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get payment by id",
            description = "Get payment by his id")
    public PaymentDto getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.SEE_OTHER)
    @Operation(summary = "Create payment session",
            description = "Endpoint for creation of payment session")
    public void createPaymentSession(@RequestBody @Valid SessionCreateDto createDto,
                                     HttpServletResponse response) {
        PaymentResponseUrlDto responseDto = paymentService.createPaymentSession(createDto);
        response.setHeader(HttpHeaders.LOCATION, responseDto.sessionUrl());
    }

    @GetMapping("/success")
    @Operation(summary = "Payment success redirection",
            description = "This endpoint is used by Stripe "
                    + "to redirect the user after a successful payment. "
                    + "It is not intended to be called directly.")
    public String paymentSuccess(@RequestParam("session_id") String sessionId) {
        if (paymentService.isPaymentStatusPaid(sessionId)) {
            return paymentService.handlePaymentSuccess(sessionId);
        }
        return paymentService.handlePaymentCanceling();
    }

    @GetMapping("/cancel")
    @Operation(summary = "Payment cancel redirection",
            description = "This endpoint is used by Stripe "
                    + "to redirect the user after a cancelled payment. "
                    + "It is not intended to be called directly.")
    public String paymentCancel() {
        return paymentService.handlePaymentCanceling();
    }
}