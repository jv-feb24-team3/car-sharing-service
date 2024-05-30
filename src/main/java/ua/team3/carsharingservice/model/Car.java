package ua.team3.carsharingservice.model;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "cars")
@Getter
@Setter
@SQLDelete(sql = "UPDATE cars SET is_deleted = true WHERE id =?")
@SQLRestriction(value = "is_deleted=false")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private String type;
    @Column(nullable = false)
    private int inventory;
    @Column(name = "daily_free", nullable = false)
    private BigDecimal dailyFree;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    private enum CarType {
        SEDAN,
        SUV,
        HATCHBACK,
        UNIVERSAL
    }
}
