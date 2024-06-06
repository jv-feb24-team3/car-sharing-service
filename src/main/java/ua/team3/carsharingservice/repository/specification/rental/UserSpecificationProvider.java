package ua.team3.carsharingservice.repository.specification.rental;

import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.repository.specification.SpecificationParam;
import ua.team3.carsharingservice.repository.specification.SpecificationProvider;

@Component
public class UserSpecificationProvider implements SpecificationProvider<Rental> {
    @Override
    public String getKey() {
        return "user";
    }

    @Override
    public Specification<Rental> build(SpecificationParam<?> param) {
        return (root, query, criteriaBuilder) -> root.get("user").get("id").in(
                Arrays.stream((Long[]) param.getValue()).toArray()
        );
    }
}
