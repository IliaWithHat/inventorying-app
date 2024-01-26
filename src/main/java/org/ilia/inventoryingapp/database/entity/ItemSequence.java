package org.ilia.inventoryingapp.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ItemSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    Long lastValue;

    @OneToOne
    User user;
}
