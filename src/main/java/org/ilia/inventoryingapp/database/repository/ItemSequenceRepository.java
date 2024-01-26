package org.ilia.inventoryingapp.database.repository;

import org.ilia.inventoryingapp.database.entity.ItemSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemSequenceRepository extends JpaRepository<ItemSequence, Integer> {

    @Query("from ItemSequence i where i.user.id = :userId")
    ItemSequence findItemSequenceByUserId(Integer userId);
}
