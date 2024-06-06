package ua.team3.carsharingservice.repository.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SpecificationParam<T> {
    private T value;
}
