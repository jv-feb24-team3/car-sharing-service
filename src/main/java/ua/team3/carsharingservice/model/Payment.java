package ua.team3.carsharingservice.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private Status status;
    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    private Type type;
    @ManyToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    private String sessionUrl;
    private String sessionId;
    @Column(nullable = false)
    private String billingDetails;
    @Column(nullable = false)
    private BigDecimal amount;

    public enum Status {
        PENDING,
        PAID,
        EXPIRED
    }

    public enum Type {
        PAYMENT,
        FINE
    }
}
