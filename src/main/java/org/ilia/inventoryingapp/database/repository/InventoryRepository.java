package org.ilia.inventoryingapp.database.repository;

import jakarta.persistence.QueryHint;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.hibernate.annotations.QueryHints.CACHEABLE;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, QuerydslPredicateExecutor<Inventory>, ItemAndInventoryRepository {

    @Modifying
    @Query("delete from Inventory i where i.user.id = :userId")
    void deleteInventoryByUserId(Integer userId);

    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))
    Optional<Inventory> findInventoryByInventoryNumberAndUser(String inventoryNumber, User user);
}
