package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    @Query("from Item i where i.inventoryNumber = :inventoryNumber and i.createdBy.id = :user")
    Optional<Item> findItemByInventoryNumberAndCreatedBy(Long inventoryNumber, Integer user);
}
