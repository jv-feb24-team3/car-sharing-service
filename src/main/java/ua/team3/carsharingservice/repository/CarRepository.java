package ua.team3.carsharingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.team3.carsharingservice.model.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
}
