package ua.team3.carsharingservice.service.impl;

import static ua.team3.carsharingservice.util.StripeConstUtil.*;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.team3.carsharingservice.config.StripeConfig;
import ua.team3.carsharingservice.dto.stripe.StripeChargeDto;
import ua.team3.carsharingservice.dto.stripe.StripeTokenDto;
import ua.team3.carsharingservice.exception.StripeChargeException;
import ua.team3.carsharingservice.exception.StripeTokenException;
import ua.team3.carsharingservice.service.StripeService;
import ua.team3.carsharingservice.util.StripeConstUtil;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    @Override
    public StripeTokenDto createCardToken(StripeTokenDto tokenDto) {
        try {
            Map<String, Object> card = new HashMap<>();
            card.put("number", tokenDto.getCardNumber());
            card.put("exp_number", tokenDto.getExpMonth());
            card.put("exp_year", tokenDto.getExpYear());
            card.put("cvc", tokenDto.getCvc());
            Map<String, Object> params = new HashMap<>();
            params.put("card", card);
            Token token = Token.create(params);
            if (token != null && token.getId() != null) {
                tokenDto.setSuccess(true);
                tokenDto.setToken(token.getId());
            }
            return tokenDto;
        } catch (StripeException e) {
            throw new StripeTokenException("Can`t create token for card "
                    + tokenDto.getCardNumber(), e);
        }
    }

    @Override
    public StripeChargeDto charge(StripeChargeDto chargeDto) {
        try {
            chargeDto.setSuccess(false);
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", chargeDto.getAmount() * CONVERSION_RATE);
            chargeParams.put("currency", CURRENCY_VALUE);
            chargeParams.put("description", "Payment for id "
                    + chargeDto.getAdditionalInfo().getOrDefault("ID_TAG", ""));
            chargeParams.put("source", chargeDto.getStripeToken());
            Map<String, Object> metaData = new HashMap<>();
            metaData.put("id", chargeDto.getChargeId());
            metaData.putAll(chargeDto.getAdditionalInfo());
            chargeParams.put("metadata", metaData);
            Charge charge = Charge.create(chargeParams);
            chargeDto.setMessage(charge.getOutcome().getSellerMessage());
            if (charge.getPaid()) {
                chargeDto.setChargeId(charge.getId());
                chargeDto.setSuccess(true);
            }
            return chargeDto;
        } catch (StripeException e) {
            throw new StripeChargeException("Can`t create charge for token "
                    + chargeDto.getStripeToken(), e);
        }
    }
}
