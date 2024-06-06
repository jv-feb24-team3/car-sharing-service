package ua.team3.carsharingservice.repository.specification.rental;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.repository.specification.SpecificationParam;
import ua.team3.carsharingservice.repository.specification.SpecificationProvider;

@Component
public class IsActiveSpecificationProvider implements SpecificationProvider<Rental> {
    @Override
    public String getKey() {
        return "isActive";
    }

    @Override
    public Specification<Rental> build(SpecificationParam<?> param) {
        return (root, query, criteriaBuilder) -> {
            Predicate statusIsActive = criteriaBuilder
                    .equal(root.get("status"), Rental.Status.ACTIVE);

            return (boolean) param.getValue() ? statusIsActive
                    : criteriaBuilder.not(statusIsActive);
        };
    }
}
