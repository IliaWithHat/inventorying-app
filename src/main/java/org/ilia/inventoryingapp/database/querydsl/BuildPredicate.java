package org.ilia.inventoryingapp.database.querydsl;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.ilia.inventoryingapp.database.entity.QItem.item;

@Component
@RequiredArgsConstructor
public class BuildPredicate {

    private final UserRepository userRepository;

    public Predicate buildPredicateByItemFilter(ItemFilter itemFilter, UserDetails userDetails) {
        Integer userId = userRepository.findUserIdByEmail(userDetails.getUsername());

        LocalDateTime showItemCreated = null;
        if (itemFilter.getShowItemCreated() != null && !"Ignore".equals(itemFilter.getShowItemCreated())) {
            switch (itemFilter.getShowItemCreated()) {
                case "1 day" -> showItemCreated = LocalDateTime.now().minusDays(1);
                case "3 day" -> showItemCreated = LocalDateTime.now().minusDays(3);
                case "1 week" -> showItemCreated = LocalDateTime.now().minusWeeks(1);
                case "2 week" -> showItemCreated = LocalDateTime.now().minusWeeks(2);
                case "1 month" -> showItemCreated = LocalDateTime.now().minusMonths(1);
                case "3 month" -> showItemCreated = LocalDateTime.now().minusMonths(3);
                case "6 month" -> showItemCreated = LocalDateTime.now().minusMonths(6);
                case "1 year" -> showItemCreated = LocalDateTime.now().minusYears(1);
            }
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

        return QPredicates.builder()
                .add(userId, item.createdBy.id::eq)
                .add(itemFilter.getName(), item.name::containsIgnoreCase)
                .add(itemFilter.getInventoryNumber(), item.inventoryNumber::eq)
                .add(itemFilter.getStoredIn(), item.storedIn::containsIgnoreCase)
                .add(itemFilter.getTimeIntervalStart() == null ? null : itemFilter.getTimeIntervalStart().atStartOfDay(), item.createdAt::goe)
                .add(itemFilter.getTimeIntervalEnd() == null ? null : itemFilter.getTimeIntervalEnd().atTime(23, 59, 59), item.createdAt::loe)
                .add(showItemCreated, item.createdAt::goe)
                .add(isOwnedByEmployee, item.isOwnedByEmployee::eq)
                .build();
    }
}
