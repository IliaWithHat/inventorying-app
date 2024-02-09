package org.ilia.inventoryingapp.database.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import jakarta.persistence.QueryHint;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.filter.ItemFilterForAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

import static org.hibernate.jpa.HibernateHints.HINT_CACHEABLE;

public interface ItemAndInventoryRepository {

    @QueryHints(@QueryHint(name = HINT_CACHEABLE, value = "true"))
    Page<Item> findItemsThatWereNotInventoried(ItemFilterForAdmin itemFilterForAdmin, User user, Integer pageNumber);

    @QueryHints(@QueryHint(name = HINT_CACHEABLE, value = "true"))
    Page<Tuple> findItemsAndInventory(Predicate predicate, Pageable pageable, User user);

    @QueryHints(@QueryHint(name = HINT_CACHEABLE, value = "true"))
    List<Tuple> findExtraInventory(Predicate predicate, User user);
}
