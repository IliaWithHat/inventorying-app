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
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.repository.InventoryRepository;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.dto.InventoryDto;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.ilia.inventoryingapp.mapper.InventoryMapper;
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

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final UserRepository userRepository;

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
    public Resource generateFullPdfByItemFilterAndUserDetails(ItemFilter itemFilter, UserDetails userDetails) {
        Path pathToTable = Files.createTempFile(null, ".pdf");
        PdfWriter writer = new PdfWriter(pathToTable.toFile());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());

        document.setMargins(20, 20, 20, 20);
        PdfFont font = PdfFontFactory.createFont("src/main/resources/font/Roboto-Regular.ttf", PdfEncodings.IDENTITY_H);
        PdfFont bold = PdfFontFactory.createFont("src/main/resources/font/Roboto-Bold.ttf", PdfEncodings.IDENTITY_H);
        Table table = new Table(new float[]{2, 7, 4, 3, 1, 2, 3, 2, 3, 2, 3, 2, 3}, true);
        table.setWidth(UnitValue.createPercentValue(100));

        int fontSize = 8;

        List<String> header = List.of("Serial number", "Item name", "Inventory number", "Stored in", "Unit");
        header.forEach(s -> table.addHeaderCell(new Cell(2, 1).add(new Paragraph(s).setFont(bold).setFontSize(fontSize))));

        List<String> header1 = List.of("According to inventory", "Actual availability", "Surplus", "Shortage");
        header1.forEach(s -> table.addHeaderCell(new Cell(1, 2).add(new Paragraph(s).setFont(bold).setFontSize(fontSize))));

        for (int i = 0; i < 4; i++) {
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setFont(bold).setFontSize(fontSize)));
            table.addHeaderCell(new Cell().add(new Paragraph("Price").setFont(bold).setFontSize(fontSize)));
        }

        document.add(table);

        int totalPages = 0;
        int pageNumber = 0;
        do {
            Page<Tuple> itemsAndInventory = inventoryRepository.findItemsAndInventory(itemFilter, userDetails, pageNumber);
            if (pageNumber == 0)
                totalPages = itemsAndInventory.getTotalPages();
            pageNumber++;

            itemsAndInventory.forEach(i -> {
                Item item = i.get(0, Item.class);
                Inventory inventory = i.get(1, Inventory.class);

                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getSerialNumber())).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(item.getName()).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(item.getInventoryNumber()).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(item.getStoredIn()).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(item.getUnit()).setFont(font).setFontSize(fontSize)));

                Double quantity = item.getQuantity();
                Double price = item.getPrice();

                Double currentQuantity = inventory == null ? 0.0 : inventory.getCurrentQuantity();
                Double currentPrice = inventory == null ? 0.0 : inventory.getCurrentPrice();

                table.addCell(new Cell().add(new Paragraph(String.valueOf(quantity)).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(price)).setFont(font).setFontSize(fontSize)));

                table.addCell(new Cell().add(new Paragraph(String.valueOf(currentQuantity)).setFont(font).setFontSize(fontSize)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(currentPrice)).setFont(font).setFontSize(fontSize)));


                if (currentQuantity > quantity) {
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(currentQuantity - quantity)).setFont(font).setFontSize(fontSize)));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(currentPrice - price)).setFont(font).setFontSize(fontSize)));

                    table.addCell(new Cell().add(new Paragraph("0.0").setFont(font).setFontSize(fontSize)));
                    table.addCell(new Cell().add(new Paragraph("0.0").setFont(font).setFontSize(fontSize)));
                } else if (quantity > currentQuantity) {
                    table.addCell(new Cell().add(new Paragraph("0.0").setFont(font).setFontSize(fontSize)));
                    table.addCell(new Cell().add(new Paragraph("0.0").setFont(font).setFontSize(fontSize)));

                    table.addCell(new Cell().add(new Paragraph(String.valueOf(quantity - currentQuantity)).setFont(font).setFontSize(fontSize)));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(price - currentPrice)).setFont(font).setFontSize(fontSize)));
                } else {
                    table.addCell(new Cell().add(new Paragraph("0.0").setFont(font).setFontSize(fontSize)));
                    table.addCell(new Cell().add(new Paragraph("0.0").setFont(font).setFontSize(fontSize)));
                    table.addCell(new Cell().add(new Paragraph("0.0").setFont(font).setFontSize(fontSize)));
                    table.addCell(new Cell().add(new Paragraph("0.0").setFont(font).setFontSize(fontSize)));
                }
            });

            if (pageNumber % 5 == 0)
                table.flush();
        } while (pageNumber < totalPages);

        table.complete();
        document.close();

        return new UrlResource(pathToTable.toUri());
    }

    public InventoryDto saveStateOfFields(InventoryDto inventoryDto, SaveField saveField) {
        return InventoryDto.builder()
                .inventoryNumber(saveField.getSaveInventoryNumber() == null ? null : inventoryDto.getInventoryNumber())
                .currentQuantity(saveField.getSaveQuantity() == null ? null : inventoryDto.getCurrentQuantity())
                .currentPrice(saveField.getSavePrice() == null ? null : inventoryDto.getCurrentPrice())
                .build();
    }
}
