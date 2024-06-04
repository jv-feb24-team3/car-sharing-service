package ua.team3.carsharingservice.util;

import java.math.BigDecimal;

public class StripeConst {
    public static final String SUCCESS_ENDPOINT = "/payments/success";
    public static final String CANCEL_ENDPOINT = "/payments/cancel";
    public static final String SESSION_ID_PARAM = "?session_id={CHECKOUT_SESSION_ID}";
    public static final String SUCCESS_MESSAGE =
            "Your payment was successful! Car rental is confirmed.";
    public static final String CANCELING_MESSAGE =
            "The payment was canceled. You can try again within 24 hours.";
    public static final String STATUS_PAID = "paid";
    public static final String CURRENCY = "USD";
    public static final Long CONVERSATION_RATE = 100L;
    public static final Long SESSION_DURATION = 24L;
    public static final Long DEFAULT_QUANTITY = 1L;
    public static final BigDecimal FINE_MULTIPLAYER = BigDecimal.valueOf(1.3);
    public static final long MIN_SESSION_LIFETIME_IN_HOURS = 1;

    private StripeConst() {
    }
}
