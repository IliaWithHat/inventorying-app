package org.ilia.inventoryingapp.database.repository;

import com.querydsl.core.types.Predicate;
import jakarta.persistence.QueryHint;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.hibernate.annotations.QueryHints.CACHEABLE;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))
    Page<Item> findAll(Predicate predicate, Pageable pageable);

    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))
    Optional<Item> findItemByIdAndUser(Long id, User user);

    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))
    Optional<Item> findItemByInventoryNumberAndUser(String inventoryNumber, User user);
}
