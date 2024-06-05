package ua.team3.carsharingservice.service.transaction;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.team3.carsharingservice.exception.NoCarsAvailableException;
import ua.team3.carsharingservice.model.Car;
import ua.team3.carsharingservice.model.Rental;
import ua.team3.carsharingservice.repository.CarRepository;
import ua.team3.carsharingservice.repository.RentalRepository;
import ua.team3.carsharingservice.service.PaymentService;

@Component
@RequiredArgsConstructor
public class RentalTransaction {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final PaymentService paymentService;

    @Transactional
    public Rental create(Rental rental) {
        Car car = getCarById(rental.getCar().getId());
        decreaseInventoryInCar(car);
        rental.setCar(car);

        Rental savedRental = rentalRepository.save(rental);
        paymentService.createPaymentForRental(savedRental);
        return savedRental;
    }

    @Transactional
    public Rental returnRental(Rental rental) {
        Rental savedRental = rentalRepository.save(rental);
        paymentService.createFinePaymentIfNeeded(savedRental);
        return savedRental;
    }

    private Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a car by id: " + id
                ));
    }

    private void decreaseInventoryInCar(Car car) {
        int availableCarInventory = car.getInventory();
        if (availableCarInventory < 1) {
            throw new NoCarsAvailableException(
                    "There are no available cars of this type, please choose another"
            );
        }
        car.setInventory(availableCarInventory - 1);
    }
}
