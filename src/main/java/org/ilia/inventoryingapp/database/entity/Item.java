package org.ilia.inventoryingapp.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@ToString(exclude = "createdBy")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    private String units;

    private Double quantity;

    private Double price;

    private Boolean isOwnedByEmployee;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private User createdBy;
}
