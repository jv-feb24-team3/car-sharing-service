package ua.team3.carsharingservice.util;

public class StripeConst {
    public static final String SUCCESS_ENDPOINT = "/api/payments/success";
    public static final String CANCEL_ENDPOINT = "/api/payments/cancel";
    public static final String SESSION_ID_PARAM = "session_id";
    public static final String SESSION_ID_VALUE = "{CHECKOUT_SESSION_ID}";
    public static final String SUCCESS_MESSAGE =
      "Your payment was successful! Your car rental is confirmed.";
    public static final String CANCELING_MESSAGE =
            "Your payment was canceled. You can complete your car rental within the next 24 hours.";
    public static final String STATUS_PAID = "paid";

    private StripeConst() {
    }
}
