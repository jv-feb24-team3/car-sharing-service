package ua.team3.carsharingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.team3.carsharingservice.dto.payment.PaymentResponseDto;
import ua.team3.carsharingservice.dto.payment.PaymentResultResponseDto;
import ua.team3.carsharingservice.model.Payment;
import ua.team3.carsharingservice.service.PaymentService;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag("Payment management")
public class PaymentController {
    private final PaymentService paymentService;


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "get payment by id",
    description = "s")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping("/{rentalId}")
    @Operation(summary = "create payment session",
            description = "Endpoint for creation of payment session")
    public void createPaymentSession(@PathVariable Long rentalId,
                                     HttpServletResponse response) {
        PaymentResponseDto responseDto = paymentService.createPaymentSession(rentalId);
        response.setHeader(HttpHeaders.LOCATION, responseDto.sessionUrl());
    }

    @GetMapping("/success")
    @Operation(summary = "Payment success",
            description = "Endpoint for successes payment")
    public PaymentResultResponseDto paymentSuccess() {
        return new PaymentResultResponseDto("Payment was successful!");
    }

    @GetMapping("/cancel")
    @Operation(summary = "Payment cancel",
            description = "Endpoint for canceled payment")
    public PaymentResultResponseDto paymentCancel() {
        return new PaymentResultResponseDto("Payment was canceled!");
    }

}
