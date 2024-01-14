package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    @Query("from Item i where i.inventoryNumber = :inventoryNumber and i.createdBy.id = :user")
    Optional<Item> findItemByInventoryNumberAndCreatedBy(String inventoryNumber, Integer user);

    @Query("select i.inventoryNumber from Item i where i.createdBy.id = :user order by i.serialNumber limit 1")
    String findFirstInventoryNumberByUserId(Integer user);
}
