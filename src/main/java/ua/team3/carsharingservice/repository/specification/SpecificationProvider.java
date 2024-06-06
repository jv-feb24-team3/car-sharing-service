package ua.team3.carsharingservice.repository.specification;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getKey();

    Specification<T> build(SpecificationParam<?> param);
}
