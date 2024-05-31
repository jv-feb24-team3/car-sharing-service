package ua.team3.carsharingservice.service;

import ua.team3.carsharingservice.dto.stripe.StripeChargeDto;
import ua.team3.carsharingservice.dto.stripe.StripeTokenDto;

public interface StripeService {
    StripeTokenDto createCardToken(StripeTokenDto tokenDto);

    StripeChargeDto charge(StripeChargeDto chargeDto);
}
