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
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.repository.InventoryRepositoryRepository;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.dto.InventoryDto;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.ilia.inventoryingapp.mapper.InventoryMapper;
import org.ilia.inventoryingapp.mapper.ItemMapper;
import org.ilia.inventoryingapp.viewUtils.SaveField;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepositoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final int fontSize = 8;

    public Page<ItemDto> findAll(UserDetails userDetails, ItemFilter itemFilter, Integer page) {
        return inventoryRepository.findItemsThatWereNotInventoried(itemFilter, userDetails, page)
                .map(itemMapper::toItemDto);
    }

    public InventoryDto create(UserDetails userDetails, InventoryDto inventoryDto) {
        Inventory inventory = inventoryMapper.toInventory(inventoryDto);

        Integer userId = userRepository.findUserIdByEmail(userDetails.getUsername());
        inventory.setUser(User.builder().id(userId).build());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toInventoryDto(savedInventory);
    }

    public void deleteInventoryByUserDetails(UserDetails userDetails) {
        Integer userId = userRepository.findUserIdByEmail(userDetails.getUsername());
        inventoryRepository.deleteInventoryByUserId(userId);
    }

    @SneakyThrows
    public Resource generateFullPdf(ItemFilter itemFilter, UserDetails userDetails) {
        Path pathToTable = Files.createTempFile(null, ".pdf");
        PdfWriter writer = new PdfWriter(pathToTable.toFile());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());

        document.setMargins(20, 20, 20, 20);
        PdfFont font = PdfFontFactory.createFont("src/main/resources/font/Roboto-Regular.ttf", PdfEncodings.IDENTITY_H);
        PdfFont bold = PdfFontFactory.createFont("src/main/resources/font/Roboto-Bold.ttf", PdfEncodings.IDENTITY_H);
        Table table = new Table(new float[]{2, 7, 4, 3, 1, 2, 3, 2, 3, 2, 3, 2, 3}, true);
        table.setWidth(UnitValue.createPercentValue(100));

        float borderWidth = 1.2F;

        List<String> header = List.of("Serial number", "Item name", "Inventory number", "Stored in", "Unit");
        header.forEach(s -> table.addHeaderCell(new Cell(2, 1).add(new Paragraph(s)
                .setFont(bold)
                .setFontSize(fontSize)
                .setTextAlignment(TextAlignment.CENTER))
                .setBorder(new SolidBorder(borderWidth))));

        List<String> header1 = List.of("According to inventory", "Actual availability", "Surplus", "Shortage");
        header1.forEach(s -> table.addHeaderCell(createCell(s, 2, bold).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(borderWidth))));

        List<String> qAndP = List.of("Quantity", "Price");
        for (int i = 0; i < 8; i++) {
            table.addHeaderCell(createCell(qAndP.get(i % 2 == 0 ? 0 : 1), 1, bold).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(borderWidth)));
        }

        document.add(table);

        int totalPages = 0;
        int pageNumber = 0;
        long totalElements = 0;
        double[] quantityAndPrice = new double[8];
        do {
            Page<Tuple> itemsAndInventory = inventoryRepository.findItemsAndInventory(itemFilter, userDetails, pageNumber);
            if (pageNumber == 0) {
                totalPages = itemsAndInventory.getTotalPages();
                totalElements = itemsAndInventory.getTotalElements();
            }
            pageNumber++;

            itemsAndInventory.forEach(i -> {
                Item item = i.get(0, Item.class);
                Inventory inventory = i.get(1, Inventory.class);

                table.addCell(createCell(String.valueOf(item.getSerialNumber()), 1, font).setBorderLeft(new SolidBorder(borderWidth)));
                table.addCell(createCell(item.getName(), 1, font));
                table.addCell(createCell(item.getInventoryNumber(), 1, font));
                table.addCell(createCell(item.getStoredIn(), 1, font));
                table.addCell(createCell(item.getUnit(), 1, font));

                double quantity = item.getQuantity();
                double price = item.getPrice();

                double currentQuantity = inventory == null ? 0.0 : inventory.getCurrentQuantity();
                double currentPrice = inventory == null ? 0.0 : inventory.getCurrentPrice();

                quantityAndPrice[0] += quantity;
                quantityAndPrice[1] += price;

                quantityAndPrice[2] += currentQuantity;
                quantityAndPrice[3] += currentPrice;

                table.addCell(createCell(String.valueOf(quantity), 1, font));
                table.addCell(createCell(String.valueOf(price), 1, font));

                table.addCell(createCell(String.valueOf(currentQuantity), 1, font));
                table.addCell(createCell(String.valueOf(currentPrice), 1, font));

                if (currentQuantity > quantity) {
                    table.addCell(createCell(String.valueOf(currentQuantity - quantity), 1, font));
                    table.addCell(createCell(String.valueOf(currentPrice - price), 1, font));

                    quantityAndPrice[4] += currentQuantity - quantity;
                    quantityAndPrice[5] += currentPrice - price;

                    table.addCell(createCell("0.0", 1, font));
                    table.addCell(createCell("0.0", 1, font).setBorderRight(new SolidBorder(borderWidth)));
                } else if (quantity > currentQuantity) {
                    table.addCell(createCell("0.0", 1, font));
                    table.addCell(createCell("0.0", 1, font));

                    table.addCell(createCell(String.valueOf(quantity - currentQuantity), 1, font));
                    table.addCell(createCell(String.valueOf(price - currentPrice), 1, font).setBorderRight(new SolidBorder(borderWidth)));

                    quantityAndPrice[6] += quantity - currentQuantity;
                    quantityAndPrice[7] += price - currentPrice;
                } else {
                    table.addCell(createCell("0.0", 1, font));
                    table.addCell(createCell("0.0", 1, font));
                    table.addCell(createCell("0.0", 1, font));
                    table.addCell(createCell("0.0", 1, font).setBorderRight(new SolidBorder(borderWidth)));
                }
            });

            if (pageNumber % 5 == 0)
                table.flush();
        } while (pageNumber < totalPages);

        table.addCell(createCell("Total", 5, bold).setBorder(new SolidBorder(borderWidth)));
        for (int i = 0; i < 8; i++) {
            table.addCell(createCell(String.valueOf(quantityAndPrice[i]), 1, bold).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(borderWidth)));
        }

        table.addCell(createCell("Total items", 5, bold).setBorder(new SolidBorder(borderWidth)));
        table.addCell(createCell(String.valueOf(totalElements), 8, bold).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(borderWidth)));

        table.complete();
        document.close();

        return new UrlResource(pathToTable.toUri());
    }

    private Cell createCell(String text, int colspan, PdfFont font) {
        return new Cell(1, colspan).add(new Paragraph(text).setFont(font).setFontSize(fontSize));
    }

    public InventoryDto saveStateOfFields(InventoryDto inventoryDto, SaveField saveField) {
        return InventoryDto.builder()
                .inventoryNumber(saveField.getSaveInventoryNumber() == null ? null : inventoryDto.getInventoryNumber())
                .currentQuantity(saveField.getSaveQuantity() == null ? null : inventoryDto.getCurrentQuantity())
                .currentPrice(saveField.getSavePrice() == null ? null : inventoryDto.getCurrentPrice())
                .build();
    }
}
