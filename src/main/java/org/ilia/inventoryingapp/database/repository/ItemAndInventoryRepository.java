package org.ilia.inventoryingapp.database.repository;

import com.querydsl.core.Tuple;
import jakarta.persistence.QueryHint;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.hibernate.annotations.QueryHints.CACHEABLE;

public interface ItemAndInventoryRepository {

    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))
    Page<Item> findItemsThatWereNotInventoried(ItemFilter itemFilter, UserDetails userDetails, Integer pageNumber);

    @QueryHints(@QueryHint(name = CACHEABLE, value = "true"))
    Page<Tuple> findItemsAndInventory(ItemFilter itemFilter, UserDetails userDetails, Integer pageNumber);

    List<Inventory> findExtraInventory(ItemFilter itemFilter, UserDetails userDetails);
}
