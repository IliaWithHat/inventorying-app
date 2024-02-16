package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.ItemSequence;
import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemSequenceRepository extends JpaRepository<ItemSequence, Integer> {

    ItemSequence findItemSequenceByUser(User user);
}
