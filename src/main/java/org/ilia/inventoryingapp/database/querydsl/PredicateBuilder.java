package org.ilia.inventoryingapp.database.querydsl;

import com.querydsl.core.types.Predicate;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

import static org.ilia.inventoryingapp.database.entity.QItem.item;
import static org.ilia.inventoryingapp.database.entity.Role.ADMIN;
import static org.ilia.inventoryingapp.filter.TimeDurationEnum.IGNORE;

@Component
public class PredicateBuilder {

    public Predicate buildPredicate(ItemFilter itemFilter, User user) {
        int userId = user.getRole() == ADMIN ? user.getId() : user.getAdmin().getId();

        LocalDateTime showItemCreated = null;
        if (itemFilter.getShowItemCreated() != null && !itemFilter.getShowItemCreated().equals(IGNORE)) {
            switch (itemFilter.getShowItemCreated()) {
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
            itemFilter.setTimeIntervalStart(null);
            itemFilter.setTimeIntervalEnd(null);
        }

        Boolean isOwnedByEmployee = null;
        if (itemFilter.getIsOwnedByEmployee() != null) {
            switch (itemFilter.getIsOwnedByEmployee()) {
                case "Yes" -> isOwnedByEmployee = true;
                case "No" -> isOwnedByEmployee = false;
            }
        }

        QPredicates qPredicates = QPredicates.builder();

        splitStringAndAddToQPredicates(itemFilter.getName(), qPredicates, item.name::containsIgnoreCase);
        splitStringAndAddToQPredicates(itemFilter.getInventoryNumber(), qPredicates, item.inventoryNumber::eq);
        splitStringAndAddToQPredicates(itemFilter.getStoredIn(), qPredicates, item.storedIn::containsIgnoreCase);

        return qPredicates
                .add(userId, item.user.id::eq)
                .add(itemFilter.getTimeIntervalStart() == null ? null : itemFilter.getTimeIntervalStart().atStartOfDay(), item.createdAt::goe)
                .add(itemFilter.getTimeIntervalEnd() == null ? null : itemFilter.getTimeIntervalEnd().atTime(23, 59, 59), item.createdAt::loe)
                .add(showItemCreated, item.createdAt::goe)
                .add(isOwnedByEmployee, item.isOwnedByEmployee::eq)
                .buildAnd();
    }

    private void splitStringAndAddToQPredicates(String string, QPredicates qPredicates, Function<String, Predicate> function) {
        QPredicates predicatesOr = QPredicates.builder();

        if (string != null && !string.isBlank() && string.contains(";")) {
            for (String oneString : string.split(";")) {
                predicatesOr.add(oneString, function);
            }
            qPredicates.add(predicatesOr.buildOr());
        } else {
            qPredicates.add(string, function);
        }
    }
}
