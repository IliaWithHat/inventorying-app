package org.ilia.inventoryingapp.database.repository;

import jakarta.persistence.QueryHint;
import org.ilia.inventoryingapp.database.entity.ItemFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.hibernate.jpa.HibernateHints.HINT_CACHEABLE;

@Repository
public interface ItemFilterRepository extends JpaRepository<ItemFilter, Integer>, QuerydslPredicateExecutor<ItemFilter> {

    @QueryHints(@QueryHint(name = HINT_CACHEABLE, value = "true"))
    Optional<ItemFilter> findItemFilterByUserId(Integer userId);

    @Modifying
    void deleteByUserId(Integer userId);
}
