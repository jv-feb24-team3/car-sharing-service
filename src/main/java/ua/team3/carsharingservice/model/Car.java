package ua.team3.carsharingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
    private CarType type;
    @Column(nullable = false)
    private int inventory;
    @Column(name = "daily_fee", nullable = false)
    private BigDecimal dailyFee;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public enum CarType {
        SEDAN,
        SUV,
        HATCHBACK,
        UNIVERSAL
    }
}
