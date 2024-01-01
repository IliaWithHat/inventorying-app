package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
