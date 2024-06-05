package ua.team3.carsharingservice.repository.specification.rental;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ua.team3.carsharingservice.model.Rental;

public class RentalSpecification {
    public static Specification<Rental> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) {
                return criteriaBuilder.conjunction();
            }
            Predicate statusIsActive = criteriaBuilder
                    .equal(root.get("status"), Rental.Status.ACTIVE);
            return isActive ? statusIsActive
                    : criteriaBuilder.not(statusIsActive);
        };
    }

    public static Specification<Rental> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("user").get("id"), userId);
        };
    }
}
