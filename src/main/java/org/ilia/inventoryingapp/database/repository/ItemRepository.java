package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    Optional<Item> findItemByIdAndUser(Long id, User user);

    @Query("from Item i where i.inventoryNumber = :inventoryNumber and (i.user = :user or i.user = (select u.admin from User u where u = :user))")
    Optional<Item> findItemByInventoryNumberAndUser(String inventoryNumber, User user);
}
