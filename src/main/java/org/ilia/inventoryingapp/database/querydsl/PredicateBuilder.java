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
        Predicate predicate;
        if (user.getAdmin() == null) {
            predicate = buildPredicateForAdmin(itemFilterForAdmin, user);
        } else {
            predicate = buildPredicateForUser(itemFilterService.findItemFilterListByUserId(user.getId()), user);
        }
        return predicate;
    }

    private Predicate buildPredicateForUser(List<ItemFilterDto> itemFilterDto, User user) {
        Boolean isOwnedByEmployee;
        switch (itemFilterDto.getFirst().getIsOwnedByEmployee()) {
            case YES -> isOwnedByEmployee = true;
            case NO -> isOwnedByEmployee = false;
            default -> isOwnedByEmployee = null;
        }

        QPredicates builder = QPredicates.builder();
        itemFilterDto.forEach(i -> builder.add(i.getStoredIn(), item.storedIn::eq));
        Predicate predicate = builder.buildOr();

        return QPredicates.builder()
                .add(user.getAdmin().getId(), item.user.id::eq)
                .add(predicate)
                .add(isOwnedByEmployee, item.isOwnedByEmployee::eq)
                .buildAnd();
    }

    private Predicate buildPredicateForAdmin(ItemFilterForAdmin itemFilterForAdmin, User user) {
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

        QPredicates qPredicates = QPredicates.builder();

        splitStringAndAddToQPredicates(itemFilterForAdmin.getName(), qPredicates, item.name::containsIgnoreCase);
        splitStringAndAddToQPredicates(itemFilterForAdmin.getInventoryNumber(), qPredicates, item.inventoryNumber::eq);
        splitStringAndAddToQPredicates(itemFilterForAdmin.getStoredIn(), qPredicates, item.storedIn::containsIgnoreCase);

        return qPredicates
                .add(user.getId(), item.user.id::eq)
                .add(itemFilterForAdmin.getTimeIntervalStart() == null ? null : itemFilterForAdmin.getTimeIntervalStart().atStartOfDay(), item.createdAt::goe)
                .add(itemFilterForAdmin.getTimeIntervalEnd() == null ? null : itemFilterForAdmin.getTimeIntervalEnd().atTime(23, 59, 59), item.createdAt::loe)
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
