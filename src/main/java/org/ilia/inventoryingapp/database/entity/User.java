package org.ilia.inventoryingapp.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@ToString(exclude = "admin")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String phone;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    private User admin;
}
