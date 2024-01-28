package org.ilia.inventoryingapp.database.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.querydsl.PredicateBuilder;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.ilia.inventoryingapp.database.entity.QInventory.inventory;
import static org.ilia.inventoryingapp.database.entity.QItem.item;

@RequiredArgsConstructor
public class ItemAndInventoryRepositoryImpl implements ItemAndInventoryRepository {

    private final PredicateBuilder predicateBuilder;
    private final EntityManager entityManager;

    @Override
    public Page<Item> findItemsThatWereNotInventoried(ItemFilter itemFilter, UserDetails userDetails, Integer pageNumber) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        Pageable pageable = PageRequest.of(pageNumber, 20, Sort.by("serialNumber"));
        Predicate predicate = predicateBuilder.buildPredicate(itemFilter, userDetails);

        List<Item> result = queryFactory
                .select(item)
                .from(item)
                .leftJoin(inventory)
                .on(item.inventoryNumber.eq(inventory.inventoryNumber).and(item.user.id.eq(inventory.user.id)))
                .where(predicate, inventory.isNull())
                .orderBy(item.serialNumber.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(item)
                .from(item)
                .leftJoin(inventory)
                .on(item.inventoryNumber.eq(inventory.inventoryNumber).and(item.user.id.eq(inventory.user.id)))
                .where(predicate, inventory.isNull())
                .fetchCount();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<Tuple> findItemsAndInventory(Predicate predicate, Pageable pageable) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Tuple> result = queryFactory
                .select(item, inventory)
                .from(item)
                .leftJoin(inventory)
                .on(item.inventoryNumber.eq(inventory.inventoryNumber).and(item.user.id.eq(inventory.user.id)))
                .where(predicate)
                .orderBy(item.serialNumber.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(item)
                .from(item)
                .leftJoin(inventory)
                .on(item.inventoryNumber.eq(inventory.inventoryNumber).and(item.user.id.eq(inventory.user.id)))
                .where(predicate)
                .fetchCount();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public List<Inventory> findExtraInventory(Predicate predicate) {
        return new JPAQuery<Inventory>(entityManager)
                .select(inventory)
                .from(item)
                .leftJoin(inventory)
                .on(item.inventoryNumber.eq(inventory.inventoryNumber).and(item.user.id.eq(inventory.user.id)))
                .where(predicate.not(), inventory.isNotNull())
                .orderBy(item.serialNumber.asc())
                .fetch();
    }
}
