package org.ilia.inventoryingapp.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.querydsl.BuildPredicate;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final BuildPredicate buildPredicate;

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
        Predicate predicate = buildPredicate.buildPredicateByItemFilter(itemFilter, userDetails);

        Pageable pageable = PageRequest.of(page, 20, Sort.by("serialNumber"));
        return itemRepository.findAll(predicate, pageable)
                .map(itemMapper::toItemDto);
    }

    @SneakyThrows
    public Resource generatePdf(ItemFilter itemFilter, UserDetails userDetails) {
        Path pathToTable = Files.createTempFile(null, ".pdf");
        PdfWriter writer = new PdfWriter(pathToTable.toFile());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());

        document.setMargins(20, 20, 20, 20);
        PdfFont font = PdfFontFactory.createFont("src/main/resources/font/Roboto-Regular.ttf", PdfEncodings.IDENTITY_H);
        PdfFont bold = PdfFontFactory.createFont("src/main/resources/font/Roboto-Bold.ttf", PdfEncodings.IDENTITY_H);
        Table table = new Table(new float[]{2, 7, 3, 3, 1, 3, 3, 3, 3}, true);
        table.setWidth(UnitValue.createPercentValue(100));

        float borderWidth = 1.2F;

        List<String> header = List.of("Serial number", "Item name", "Inventory number", "Stored in", "Unit", "Price per unit", "Quantity", "Sum", "Owned by employee");
        header.forEach(s -> table.addHeaderCell(createCell(s, 1, bold).setBorder(new SolidBorder(borderWidth)).setTextAlignment(TextAlignment.CENTER)));

        document.add(table);

        Predicate predicate = buildPredicate.buildPredicateByItemFilter(itemFilter, userDetails);
        int totalPages = 0;
        int pageNumber = 0;
        long totalElements = 0;
        List<BigDecimal> quantityAndSum = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            quantityAndSum.add(new BigDecimal(0));
        }
        do {
            Pageable pageable = PageRequest.of(pageNumber, 50, Sort.by("serialNumber"));
            Page<Item> items = itemRepository.findAll(predicate, pageable);
            if (pageNumber == 0) {
                totalPages = items.getTotalPages();
                totalElements = items.getTotalElements();
            }
            pageNumber++;

            items.forEach(i -> {
                BigDecimal sum = i.getQuantity().multiply(i.getPricePerUnit()).setScale(2, RoundingMode.HALF_UP);

                table.addCell(createCell(i.getSerialNumber().toString(), 1, font).setBorderLeft(new SolidBorder(borderWidth)));
                table.addCell(createCell(i.getName(), 1, font));
                table.addCell(createCell(i.getInventoryNumber(), 1, font));
                table.addCell(createCell(i.getStoredIn(), 1, font));
                table.addCell(createCell(i.getUnit(), 1, font));
                table.addCell(createCell(i.getPricePerUnit().toString(), 1, font));
                table.addCell(createCell(i.getQuantity().toString(), 1, font));
                table.addCell(createCell(sum.toString(), 1, font));
                table.addCell(createCell(i.getIsOwnedByEmployee() ? "Yes" : "No", 1, font).setBorderRight(new SolidBorder(borderWidth)));

                quantityAndSum.set(0, quantityAndSum.get(0).add(i.getQuantity()));
                quantityAndSum.set(1, quantityAndSum.get(1).add(sum));
            });

            if (pageNumber % 5 == 0)
                table.flush();
        } while (pageNumber < totalPages);

        table.addCell(createCell("Total", 6, bold).setBorder(new SolidBorder(borderWidth)));
        for (int i = 0; i < 2; i++) {
            table.addCell(createCell(quantityAndSum.get(i).toString(), 1, bold).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(borderWidth)));
        }

        table.addCell(createCell("", 1, font).setBorder(new SolidBorder(borderWidth)));

        table.addCell(createCell("Total items", 6, bold).setBorder(new SolidBorder(borderWidth)));
        table.addCell(createCell(String.valueOf(totalElements), 2, bold).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(borderWidth)));

        table.addCell(createCell("", 1, font).setBorder(new SolidBorder(borderWidth)));

        table.complete();
        document.close();

        return new UrlResource(pathToTable.toUri());
    }

    private Cell createCell(String text, int colspan, PdfFont font) {
        return new Cell(1, colspan).add(new Paragraph(text).setFont(font).setFontSize(8));
    }

    public Optional<ItemDto> findById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toItemDto);
    }

    @Transactional
    public ItemDto create(UserDetails userDetails, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);

        //TODO enable auditing
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
                .unit(saveField.getSaveUnit() == null ? null : itemDto.getUnit())
                .quantity(saveField.getSaveQuantity() == null ? null : itemDto.getQuantity())
                .pricePerUnit(saveField.getSavePrice() == null ? null : itemDto.getPricePerUnit())
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
