package org.ilia.inventoryingapp.database.repository;

import com.querydsl.core.types.Predicate;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, QuerydslPredicateExecutor<Inventory> {

//    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))

    @Modifying
    @Query("delete from Inventory i where i.user.id = :userId")
    void deleteInventoryByUserId(Integer userId);

    Optional<Inventory> findInventoryByInventoryNumberAndUser(String inventoryNumber, User user);

    //TODO create query
    Page<Object[]> findItemsAndInventory(Predicate predicate, Pageable pageable);
}
