package org.ilia.inventoryingapp.database.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.ilia.inventoryingapp.database.entity.QInventory.inventory;
import static org.ilia.inventoryingapp.database.entity.QItem.item;

@RequiredArgsConstructor
public class ItemAndInventoryRepositoryImpl implements ItemAndInventoryRepository {

    private final EntityManager entityManager;

    @Override
    public Page<Item> findItemsThatWereNotInventoried(Predicate predicate, Pageable pageable, User user) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Item> result = queryFactory
                .select(item)
                .from(item)
                .leftJoin(inventory)
                .on(item.inventoryNumber.eq(inventory.inventoryNumber).and(inventory.user.eq(user)))
                .where(predicate, inventory.isNull())
                .orderBy(item.serialNumber.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(item)
                .from(item)
                .leftJoin(inventory)
                .on(item.inventoryNumber.eq(inventory.inventoryNumber).and(inventory.user.eq(user)))
                .where(predicate, inventory.isNull())
                .fetchCount();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<Tuple> findItemsAndInventory(Predicate predicate, Pageable pageable, User user) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Tuple> result = queryFactory
                .select(item, inventory)
                .from(item)
                .leftJoin(inventory)
                .on(item.inventoryNumber.eq(inventory.inventoryNumber).and(inventory.user.eq(user)))
                .where(predicate)
                .orderBy(item.serialNumber.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(item)
                .from(item)
                .leftJoin(inventory)
                .on(item.inventoryNumber.eq(inventory.inventoryNumber).and(inventory.user.eq(user)))
                .where(predicate)
                .fetchCount();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public List<Tuple> findExtraInventory(Predicate[] predicates, User user) {
        if (predicates[1] == null)
            return new ArrayList<>();
        return new JPAQuery<Inventory>(entityManager)
                .select(item, inventory)
                .from(item)
                .leftJoin(inventory)
                .on(item.inventoryNumber.eq(inventory.inventoryNumber).and(inventory.user.eq(user)))
                .where(predicates[0], predicates[1].not(), inventory.isNotNull())
                .orderBy(item.serialNumber.asc())
                .fetch();
    }
}
