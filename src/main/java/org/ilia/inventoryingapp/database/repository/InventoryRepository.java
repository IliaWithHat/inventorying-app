package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, QuerydslPredicateExecutor<Inventory>, ItemAndInventoryRepository {

    @Modifying
    void deleteInventoryByUser(User user);

    Optional<Inventory> findInventoryByInventoryNumberAndUser(String inventoryNumber, User user);
}
