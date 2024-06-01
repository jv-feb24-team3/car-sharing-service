package ua.team3.carsharingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.team3.carsharingservice.config.MapperConfig;
import ua.team3.carsharingservice.dto.stripe.payment.PaymentDto;
import ua.team3.carsharingservice.model.Payment;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "rental.id", target = "rentalId")
    PaymentDto toDto(Payment payment);
}
