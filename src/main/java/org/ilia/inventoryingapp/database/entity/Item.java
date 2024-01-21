package org.ilia.inventoryingapp.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString(exclude = "createdBy")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long serialNumber;

    private String name;

    private String inventoryNumber;

    private String storedIn;

    private String unit;

    private BigDecimal quantity;

    private BigDecimal pricePerUnit;

    private Boolean isOwnedByEmployee;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private User createdBy;
}
