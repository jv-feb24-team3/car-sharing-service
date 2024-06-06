package ua.team3.carsharingservice.repository.specification.rental;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.repository.specification.SpecificationProvider;
import ua.team3.carsharingservice.repository.specification.SpecificationProviderManager;

@Component
@RequiredArgsConstructor
public class RentalSpecificationProviderManager implements SpecificationProviderManager<Rental> {
    private final List<SpecificationProvider<Rental>> rentalSpecificationProviders;

    @Override
    public SpecificationProvider<Rental> getSpecificationProvider(String key) {
        return rentalSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Can't find specification provider for key: " + key)
                );
    }
}
