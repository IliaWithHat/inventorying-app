package org.ilia.inventoryingapp.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigDecimal;

@Data
@ToString(exclude = "user")
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String inventoryNumber;

    private BigDecimal currentQuantity;

    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private User user;
}
