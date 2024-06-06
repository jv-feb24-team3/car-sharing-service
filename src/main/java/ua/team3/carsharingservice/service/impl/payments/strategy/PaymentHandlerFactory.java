package ua.team3.carsharingservice.service.impl.payments.strategy;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.service.PaymentHandler;

@Component
@RequiredArgsConstructor
public class PaymentHandlerFactory {
    private final Map<String, PaymentHandler> handlerMap;

    public PaymentHandler getHandler(String paymentType) {
        return handlerMap.get(paymentType);
    }
}
