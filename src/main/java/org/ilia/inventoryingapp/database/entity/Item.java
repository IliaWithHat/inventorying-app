package org.ilia.inventoryingapp.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString(exclude = "user")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long serialNumber;

    private String name;

    private String inventoryNumber;

    private String storedIn;

    @Enumerated(value = EnumType.STRING)
    private Unit unit;

    private BigDecimal quantity;

    private BigDecimal pricePerUnit;

    private Boolean isOwnedByEmployee;

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
