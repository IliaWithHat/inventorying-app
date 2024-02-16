package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.ItemFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemFilterRepository extends JpaRepository<ItemFilter, Integer>, QuerydslPredicateExecutor<ItemFilter> {

    List<ItemFilter> findItemFilterByUserId(Integer userId);

    @Modifying
    void deleteByUserId(Integer userId);
}
