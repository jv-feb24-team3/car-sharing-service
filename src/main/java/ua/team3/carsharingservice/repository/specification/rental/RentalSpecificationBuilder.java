package ua.team3.carsharingservice.repository.specification.rental;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.dto.RentalSearchParameters;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.repository.specification.SpecificationBuilder;
import ua.team3.carsharingservice.repository.specification.SpecificationParam;
import ua.team3.carsharingservice.repository.specification.SpecificationProviderManager;

@Component
@RequiredArgsConstructor
public class RentalSpecificationBuilder implements SpecificationBuilder<Rental> {
    private final SpecificationProviderManager<Rental> specificationProviderManager;

    @Override
    public Specification<Rental> build(RentalSearchParameters searchParameters) {
        Specification<Rental> specification = Specification.where(null);
        if (searchParameters.getUserIds() != null && searchParameters.getUserIds().length > 0) {
            SpecificationParam<Long[]> userIds =
                    new SpecificationParam<>(searchParameters.getUserIds());
            specification = specification.and(
                    specificationProviderManager.getSpecificationProvider("user")
                            .build(userIds));
        }
        if (searchParameters.getIsActive() != null) {
            SpecificationParam<Boolean> isActive =
                    new SpecificationParam<>(searchParameters.getIsActive());
            specification = specification.and(
                    specificationProviderManager.getSpecificationProvider("isActive")
                            .build(isActive));
        }
        return specification;
    }
}
