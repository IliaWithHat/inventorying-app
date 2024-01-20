package org.ilia.inventoryingapp.database.repository;

import com.querydsl.core.Tuple;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;

public interface ItemAndInventoryRepository {

    Page<Item> findItemsThatWereNotInventoried(ItemFilter itemFilter, UserDetails userDetails, Integer pageNumber);

    Page<Tuple> findItemsAndInventory(ItemFilter itemFilter, UserDetails userDetails, Integer pageNumber);
}
