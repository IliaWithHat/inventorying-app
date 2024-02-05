package org.ilia.inventoryingapp.database.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import jakarta.persistence.QueryHint;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

import static org.hibernate.annotations.QueryHints.CACHEABLE;

public interface ItemAndInventoryRepository {

    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))
    Page<Item> findItemsThatWereNotInventoried(ItemFilter itemFilter, User user, Integer pageNumber);

    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))
    Page<Tuple> findItemsAndInventory(Predicate predicate, Pageable pageable);

    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))
    List<Tuple> findExtraInventory(Predicate predicate);
}
