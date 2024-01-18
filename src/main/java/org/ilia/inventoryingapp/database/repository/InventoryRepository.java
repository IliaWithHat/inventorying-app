package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, QuerydslPredicateExecutor<Inventory> {

//    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))

    @Modifying
    @Query("delete from Inventory i where i.user.id = :userId")
    void deleteInventoryByUserId(Integer userId);
}
