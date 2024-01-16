package org.ilia.inventoryingapp.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.querydsl.QPredicates;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.ilia.inventoryingapp.mapper.ItemMapper;
import org.ilia.inventoryingapp.viewUtils.SaveField;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.ilia.inventoryingapp.database.entity.QItem.item;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public Page<ItemDto> findLastFiveItems(UserDetails userDetails) {
        Integer userId = userRepository.findUserIdByEmail(userDetails.getUsername());
        Predicate predicate = QPredicates.builder()
                .add(userId, item.createdBy.id::eq)
                .build();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("serialNumber").descending());
        return itemRepository.findAll(predicate, pageable)
                .map(itemMapper::toItemDto);
    }

    //TODO результаты с самым высоким совпадением должны быть на первом месте
    public Page<ItemDto> findAll(UserDetails userDetails, ItemFilter itemFilter, Integer page) {
        Predicate predicate = buildPredicateByItemFilter(itemFilter, userDetails);

        Pageable pageable = PageRequest.of(page, 20, Sort.by("serialNumber"));
        return itemRepository.findAll(predicate, pageable)
                .map(itemMapper::toItemDto);
    }

    @SneakyThrows
    public Resource loadResource(ItemFilter itemFilter, UserDetails userDetails) {
        Path pathToTable = Files.createTempFile(null, ".pdf");
        PdfWriter writer = new PdfWriter(pathToTable.toFile());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());

        document.setMargins(20, 20, 20, 20);
        PdfFont font = PdfFontFactory.createFont("src/main/resources/font/Roboto-Regular.ttf", PdfEncodings.IDENTITY_H);
        PdfFont bold = PdfFontFactory.createFont("src/main/resources/font/Roboto-Bold.ttf", PdfEncodings.IDENTITY_H);
        Table table = new Table(new float[]{2, 7, 4, 3, 2, 2, 3, 2, 3}, true);
        table.setWidth(UnitValue.createPercentValue(100));

        int fontSize = 8;
        List<String> header = List.of("Serial number", "Item name", "Inventory number", "Stored in", "Units", "Quantity", "Price", "Created at", "Owned by employee");
        header.forEach(s -> table.addHeaderCell(new Cell().add(new Paragraph(s).setFont(bold).setFontSize(fontSize))));

        document.add(table);

        Predicate predicate = buildPredicateByItemFilter(itemFilter, userDetails);
        int totalPages = 0;
        int pageNumber = 0;
        do {
            Pageable pageable = PageRequest.of(pageNumber, 50, Sort.by("serialNumber"));
            Page<Item> items = itemRepository.findAll(predicate, pageable);
            if (pageNumber == 0)
                totalPages = items.getTotalPages();
            pageNumber++;

            items.forEach(i -> {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(i.getSerialNumber())).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(i.getName()).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(i.getInventoryNumber()).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(i.getStoredIn()).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(i.getUnits()).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(i.getQuantity())).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(i.getPrice())).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(i.getCreatedAt().toLocalDate())).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(i.getIsOwnedByEmployee() ? "Yes" : "No").setFont(font).setFontSize(fontSize)));
            });

            if (pageNumber % 5 == 0)
                table.flush();
        } while (pageNumber < totalPages);

        table.complete();
        document.close();

        return new UrlResource(pathToTable.toUri());
    }

    private Predicate buildPredicateByItemFilter(ItemFilter itemFilter, UserDetails userDetails) {
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

    public Optional<ItemDto> findById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toItemDto);
    }

    @Transactional
    public ItemDto create(UserDetails userDetails, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);

        item.setCreatedAt(LocalDateTime.now());
        Integer userId = userRepository.findUserIdByEmail(userDetails.getUsername());
        item.setCreatedBy(User.builder().id(userId).build());

        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Transactional
    public Optional<ItemDto> update(ItemDto itemDto, Long id) {
        return itemRepository.findById(id)
                .map(item -> itemMapper.copyItemDtoToItem(itemDto, item))
                .map(itemRepository::saveAndFlush)
                .map(itemMapper::toItemDto);
    }

    @Transactional
    public boolean delete(Long id) {
        if (itemRepository.findById(id).isPresent()) {
            itemRepository.deleteById(id);
            itemRepository.flush();
            return true;
        }
        return false;
    }

    public ItemDto saveStateOfFields(ItemDto itemDto, SaveField saveField) {
        String inventoryNumber;
        if (saveField.getAutoincrement() != null) {
            inventoryNumber = incrementStringNumber(itemDto.getInventoryNumber());
        } else {
            inventoryNumber = itemDto.getInventoryNumber();
        }
        return ItemDto.builder()
                .name(saveField.getSaveName() == null ? null : itemDto.getName())
                .inventoryNumber(saveField.getSaveInventoryNumber() == null ? null : inventoryNumber)
                .storedIn(saveField.getSaveStoredIn() == null ? null : itemDto.getStoredIn())
                .units(saveField.getSaveUnits() == null ? null : itemDto.getUnits())
                .quantity(saveField.getSaveQuantity() == null ? null : itemDto.getQuantity())
                .price(saveField.getSavePrice() == null ? null : itemDto.getPrice())
                .isOwnedByEmployee(saveField.getSaveIsOwnedByEmployee() == null ? null : itemDto.getIsOwnedByEmployee())
                .build();
    }

    private String incrementStringNumber(String inventoryNumber) {
        try {
            if (inventoryNumber.contains(".")) {
                String numberBeforeDot = inventoryNumber.substring(0, inventoryNumber.lastIndexOf(".") + 1);
                String numberAfterDot = inventoryNumber.substring(inventoryNumber.lastIndexOf(".") + 1);
                long number = Long.parseLong(numberAfterDot) + 1;
                return numberBeforeDot + "0".repeat(numberAfterDot.length() - String.valueOf(number).length()) + number;
            } else if (inventoryNumber.contains("-")) {
                String numberBeforeHyphen = inventoryNumber.substring(0, inventoryNumber.indexOf("-") + 1);
                String numberAfterHyphen = inventoryNumber.substring(inventoryNumber.indexOf("-") + 1);
                long number = Long.parseLong(numberAfterHyphen) + 1;
                return numberBeforeHyphen + "0".repeat(numberAfterHyphen.length() - String.valueOf(number).length()) + number;
            } else if (inventoryNumber.charAt(0) == '0') {
                long number = Long.parseLong(inventoryNumber) + 1;
                return "0".repeat(inventoryNumber.length() - String.valueOf(number).length()) + number;
            } else {
                long number = Long.parseLong(inventoryNumber) + 1;
                return String.valueOf(number);
            }
        } catch (Exception e) {
            log.warn("Error increment this string: {}", inventoryNumber);
            return "";
        }
    }
}
