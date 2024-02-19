package org.ilia.inventoryingapp.database.querydsl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.ItemFilterDto;
import org.ilia.inventoryingapp.filter.ItemFilterForAdmin;
import org.ilia.inventoryingapp.service.ItemFilterService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

import static org.ilia.inventoryingapp.database.entity.QItem.item;
import static org.ilia.inventoryingapp.filter.TimeDurationEnum.IGNORE;

@Component
@RequiredArgsConstructor
public class PredicateBuilder {

    private final ItemFilterService itemFilterService;

    public Predicate buildPredicate(User user, ItemFilterForAdmin itemFilterForAdmin) {
        return buildPredicate(user, itemFilterForAdmin, false)[0];
    }

    public Predicate[] buildPredicate(User user, ItemFilterForAdmin itemFilterForAdmin, boolean splitPredicate) {
        Predicate[] predicates;
        if (user.getAdmin() == null) {
            predicates = buildPredicateForAdmin(itemFilterForAdmin, user, splitPredicate);
        } else {
            predicates = buildPredicateForUser(itemFilterService.findItemFilterListByUserId(user.getId()), user, splitPredicate);
        }
        //If filter empty, predicates[1] == null
        return predicates;
    }

    private Predicate[] buildPredicateForUser(List<ItemFilterDto> itemFilterDto, User user, boolean splitPredicate) {
        Boolean isOwnedByEmployee;
        switch (itemFilterDto.getFirst().getIsOwnedByEmployee()) {
            case YES -> isOwnedByEmployee = true;
            case NO -> isOwnedByEmployee = false;
            default -> isOwnedByEmployee = null;
        }

        QPredicates qPredicatesOr = QPredicates.builder();
        itemFilterDto.forEach(i -> qPredicatesOr.add(i.getStoredIn(), item.storedIn::eq));
        Predicate storedInPredicate = qPredicatesOr.buildOr();

        QPredicates qPredicatesAnd = QPredicates.builder()
                .add(storedInPredicate)
                .add(isOwnedByEmployee, item.isOwnedByEmployee::eq);

        if (splitPredicate) {
            return new Predicate[]{item.user.eq(user.getAdmin()), qPredicatesAnd.buildAnd()};
        } else {
            return new Predicate[]{qPredicatesAnd.add(item.user.eq(user.getAdmin())).buildAnd()};
        }
    }

    private Predicate[] buildPredicateForAdmin(ItemFilterForAdmin itemFilterForAdmin, User user, boolean splitPredicate) {
        LocalDateTime showItemCreated = null;
        if (itemFilterForAdmin.getShowItemCreated() != null && !itemFilterForAdmin.getShowItemCreated().equals(IGNORE)) {
            switch (itemFilterForAdmin.getShowItemCreated()) {
                case ONE_DAY -> showItemCreated = LocalDateTime.now().minusDays(1);
                case THREE_DAYS -> showItemCreated = LocalDateTime.now().minusDays(3);
                case ONE_WEEK -> showItemCreated = LocalDateTime.now().minusWeeks(1);
                case TWO_WEEKS -> showItemCreated = LocalDateTime.now().minusWeeks(2);
                case ONE_MONTH -> showItemCreated = LocalDateTime.now().minusMonths(1);
                case TREE_MONTHS -> showItemCreated = LocalDateTime.now().minusMonths(3);
                case SIX_MONTHS -> showItemCreated = LocalDateTime.now().minusMonths(6);
                case ONE_YEAR -> showItemCreated = LocalDateTime.now().minusYears(1);
            }
            showItemCreated = showItemCreated.with(LocalTime.MIDNIGHT).plusDays(1);
            itemFilterForAdmin.setTimeIntervalStart(null);
            itemFilterForAdmin.setTimeIntervalEnd(null);
        }

        Boolean isOwnedByEmployee = null;
        if (itemFilterForAdmin.getIsOwnedByEmployee() != null) {
            switch (itemFilterForAdmin.getIsOwnedByEmployee()) {
                case YES -> isOwnedByEmployee = true;
                case NO -> isOwnedByEmployee = false;
            }
        }

        QPredicates qPredicatesAnd = QPredicates.builder()
                .add(itemFilterForAdmin.getTimeIntervalStart() == null ? null : itemFilterForAdmin.getTimeIntervalStart().atStartOfDay(), item.createdAt::goe)
                .add(itemFilterForAdmin.getTimeIntervalEnd() == null ? null : itemFilterForAdmin.getTimeIntervalEnd().atTime(23, 59, 59), item.createdAt::loe)
                .add(showItemCreated, item.createdAt::goe)
                .add(isOwnedByEmployee, item.isOwnedByEmployee::eq);

        splitStringAndAddToQPredicates(itemFilterForAdmin.getName(), qPredicatesAnd, item.name::containsIgnoreCase);
        splitStringAndAddToQPredicates(itemFilterForAdmin.getInventoryNumber(), qPredicatesAnd, item.inventoryNumber::eq);
        splitStringAndAddToQPredicates(itemFilterForAdmin.getStoredIn(), qPredicatesAnd, item.storedIn::containsIgnoreCase);

        if (splitPredicate) {
            return new Predicate[]{item.user.eq(user), qPredicatesAnd.buildAnd()};
        } else {
            return new Predicate[]{qPredicatesAnd.add(item.user.eq(user)).buildAnd()};
        }
    }

    private void splitStringAndAddToQPredicates(String string, QPredicates qPredicatesAnd, Function<String, Predicate> function) {
        QPredicates predicatesOr = QPredicates.builder();

        if (string != null && !string.isBlank() && string.contains(";")) {
            for (String oneString : string.split(";")) {
                predicatesOr.add(oneString, function);
            }
            qPredicatesAnd.add(predicatesOr.buildOr());
        } else {
            qPredicatesAnd.add(string, function);
        }
    }
}
