package org.ilia.inventoryingapp.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@ToString(exclude = "user")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ItemSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    Long lastValue;

    @OneToOne(fetch = FetchType.LAZY)
    User user;
}
