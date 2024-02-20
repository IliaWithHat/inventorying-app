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
public class ItemFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String storedIn;

    @Enumerated(EnumType.STRING)
    private OptionsForIsOwnedByEmployee isOwnedByEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
