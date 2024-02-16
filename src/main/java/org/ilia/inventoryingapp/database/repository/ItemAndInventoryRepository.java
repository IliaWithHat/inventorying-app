package org.ilia.inventoryingapp.database.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.filter.ItemFilterForAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemAndInventoryRepository {

    Page<Item> findItemsThatWereNotInventoried(ItemFilterForAdmin itemFilterForAdmin, User user, Integer pageNumber);

    Page<Tuple> findItemsAndInventory(Predicate predicate, Pageable pageable, User user);

    List<Tuple> findExtraInventory(Predicate predicate, User user);
}
