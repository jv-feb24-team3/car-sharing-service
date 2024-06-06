package ua.team3.carsharingservice.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ua.team3.carsharingservice.dto.RentalSearchParameters;

public interface SpecificationBuilder<T> {
    Specification<T> build(RentalSearchParameters searchParameters);
}
