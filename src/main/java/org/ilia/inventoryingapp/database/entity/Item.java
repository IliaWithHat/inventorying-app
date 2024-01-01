package org.ilia.inventoryingapp.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number")
    private Long serialNumber;

    @Column(name = "inventory_number")
    private Long inventoryNumber;

    private String name;

    @Column(name = "stored_in")
    private String storedIn;

    private Integer quantity;

    @Column(name = "is_owned_by_employee")
    private Boolean isOwnedByEmployee;

    @Column(name = "additional_info")
    @JdbcTypeCode(value = SqlTypes.JSON)
    private String additionalInfo;

    private String image;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by")
    @ManyToOne
    @JoinColumn(name = "id")
    private User createdBy;

    @LastModifiedDate
    @Column(name = "modified_at")
    private Instant modifiedAt;

    @LastModifiedBy
    @Column(name = "modified_by")
    @ManyToOne
    @JoinColumn(name = "id")
    private User modifiedBy;
}




















