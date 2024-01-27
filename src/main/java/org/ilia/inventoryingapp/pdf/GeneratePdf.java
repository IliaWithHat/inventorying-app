package org.ilia.inventoryingapp.pdf;

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
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.querydsl.PredicateBuilder;
import org.ilia.inventoryingapp.database.repository.InventoryRepository;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class GeneratePdf {

    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final PredicateBuilder predicateBuilder;
    private final int fontSize = 8;
    private final float borderWidth = 1.2F;
    private PdfFont font;
    private PdfFont bold;

    @SneakyThrows
    public Resource generatePdfForExport(ItemFilter itemFilter, UserDetails userDetails) {
        Object[] pathAndDocument = prepareDocument();
        Path pathToFile = (Path) pathAndDocument[0];
        Document document = (Document) pathAndDocument[1];

        Table table = new Table(new float[]{2, 7, 3, 3, 1, 3, 3, 3, 3}, true);
        table.setWidth(UnitValue.createPercentValue(100));

        List.of("Serial number", "Item name", "Inventory number", "Stored in", "Unit", "Price per unit", "Quantity", "Sum", "Owned by employee")
                .forEach(s -> table
                        .addHeaderCell(createCell(s, 1, 1, bold, true)
                                .setBorder(new SolidBorder(borderWidth))
                                .setTextAlignment(TextAlignment.CENTER)));

        document.add(table);

        int totalPages = 0;
        int pageNumber = 0;
        long totalElements = 0;
        List<BigDecimal> quantityAndSum = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            quantityAndSum.add(new BigDecimal(i % 2 == 0 ? "0.000" : "0.00"));
        }

        Predicate predicate = predicateBuilder.buildPredicate(itemFilter, userDetails);
        do {
            Pageable pageable = PageRequest.of(pageNumber, 50, Sort.by("serialNumber"));
            Page<Item> items = itemRepository.findAll(predicate, pageable);
            if (pageNumber == 0) {
                totalPages = items.getTotalPages();
                totalElements = items.getTotalElements();
            }
            pageNumber++;

            items.forEach(item -> {
                BigDecimal sum = item.getQuantity().multiply(item.getPricePerUnit()).setScale(2, RoundingMode.HALF_UP);

                table.addCell(createCell(item.getSerialNumber().toString()).setBorderLeft(new SolidBorder(borderWidth)));
                table.addCell(createCell(item.getName()));
                table.addCell(createCell(item.getInventoryNumber()));
                table.addCell(createCell(item.getStoredIn()));
                table.addCell(createCell(item.getUnit().toString()));
                table.addCell(createCell(item.getPricePerUnit().toString()));
                table.addCell(createCell(item.getQuantity().toString()));
                table.addCell(createCell(sum.toString()));
                table.addCell(createCell(item.getIsOwnedByEmployee() ? "Yes" : "No").setBorderRight(new SolidBorder(borderWidth)));

                quantityAndSum.set(0, quantityAndSum.get(0).add(item.getQuantity()));
                quantityAndSum.set(1, quantityAndSum.get(1).add(sum));
            });

            if (pageNumber % 5 == 0)
                table.flush();
        } while (pageNumber < totalPages);

        table.addCell(createCell("Total", 1, 6, bold, true)
                .setBorder(new SolidBorder(borderWidth)));
        for (int i = 0; i < 2; i++) {
            table.addCell(createCell(quantityAndSum.get(i).toString(), 1, 1, bold, true)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(borderWidth)));
        }

        table.addCell(createCell("", 1, 1, bold, true)
                .setBorder(new SolidBorder(borderWidth)));

        table.addCell(createCell("Total items", 1, 6, bold, true)
                .setBorder(new SolidBorder(borderWidth)));
        table.addCell(createCell(String.valueOf(totalElements), 1, 2, bold, true)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(borderWidth)));

        table.addCell(createCell("", 1, 1, bold, true)
                .setBorder(new SolidBorder(borderWidth)));

        table.complete();
        document.close();

        return new UrlResource(pathToFile.toUri());
    }

    @SneakyThrows
    public Resource generatePdfAfterInventory(ItemFilter itemFilter, UserDetails userDetails) {
        Object[] pathAndDocument = prepareDocument();
        Path pathToFile = (Path) pathAndDocument[0];
        Document document = (Document) pathAndDocument[1];

        Table table = new Table(new float[]{2, 7, 4, 3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3}, true);
        table.setWidth(UnitValue.createPercentValue(100));

        List.of("Serial number", "Item name", "Inventory number", "Stored in", "Unit", "Price per unit")
                .forEach(s -> table.
                        addHeaderCell(createCell(s, 2, 1, bold, true)
                                .setBorder(new SolidBorder(borderWidth))
                                .setTextAlignment(TextAlignment.CENTER)));

        List.of("According to inventory", "Actual availability", "Surplus", "Shortage")
                .forEach(s -> table.
                        addHeaderCell(createCell(s, 1, 2, bold, true)
                                .setBorder(new SolidBorder(borderWidth))
                                .setTextAlignment(TextAlignment.CENTER)));

        List<String> qAndS = List.of("Quantity", "Sum");
        for (int i = 0; i < 8; i++) {
            table.addHeaderCell(createCell(qAndS.get(i % 2 == 0 ? 0 : 1), 1, 1, bold, true)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(borderWidth)));
        }

        document.add(table);

        int totalPages = 0;
        int pageNumber = 0;
        long totalElements = 0;
        List<BigDecimal> quantityAndSum = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            quantityAndSum.add(new BigDecimal(i % 2 == 0 ? "0.000" : "0.00"));
        }

        Predicate predicate = predicateBuilder.buildPredicate(itemFilter, userDetails);
        do {
            Pageable pageable = PageRequest.of(pageNumber, 50, Sort.by("serialNumber"));
            Page<Tuple> itemsAndInventory = inventoryRepository.findItemsAndInventory(predicate, pageable);
            if (pageNumber == 0) {
                totalPages = itemsAndInventory.getTotalPages();
                totalElements = itemsAndInventory.getTotalElements();
            }
            pageNumber++;

            itemsAndInventory.forEach(tuple -> {
                Item item = tuple.get(0, Item.class);
                Inventory inventory = tuple.get(1, Inventory.class);

                table.addCell(createCell(item.getSerialNumber().toString()).setBorderLeft(new SolidBorder(borderWidth)));
                table.addCell(createCell(item.getName()));
                table.addCell(createCell(item.getInventoryNumber()));
                table.addCell(createCell(item.getStoredIn()));
                table.addCell(createCell(item.getUnit().toString()));
                table.addCell(createCell(item.getPricePerUnit().toString()));

                BigDecimal quantity = item.getQuantity();
                BigDecimal currentQuantity = inventory == null ? new BigDecimal("0.000") : inventory.getCurrentQuantity();

                BigDecimal sum = quantity.multiply(item.getPricePerUnit()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal currentSum = currentQuantity.multiply(item.getPricePerUnit()).setScale(2, RoundingMode.HALF_UP);

                table.addCell(createCell(quantity.toString()));
                table.addCell(createCell(sum.toString()));

                table.addCell(createCell(currentQuantity.toString()));
                table.addCell(createCell(currentSum.toString()));

                quantityAndSum.set(0, quantityAndSum.get(0).add(quantity));
                quantityAndSum.set(1, quantityAndSum.get(1).add(sum));

                quantityAndSum.set(2, quantityAndSum.get(2).add(currentQuantity));
                quantityAndSum.set(3, quantityAndSum.get(3).add(currentSum));

                if (currentQuantity.compareTo(quantity) > 0) {
                    BigDecimal surplusQuantity = currentQuantity.subtract(quantity);
                    BigDecimal surplusSum = currentSum.subtract(sum);

                    table.addCell(createCell(surplusQuantity.toString()));
                    table.addCell(createCell(surplusSum.toString()));

                    quantityAndSum.set(4, quantityAndSum.get(4).add(surplusQuantity));
                    quantityAndSum.set(5, quantityAndSum.get(5).add(surplusSum));

                    table.addCell(createCell("0.000"));
                    table.addCell(createCell("0.00").setBorderRight(new SolidBorder(borderWidth)));
                } else if (quantity.compareTo(currentQuantity) > 0) {
                    table.addCell(createCell("0.000"));
                    table.addCell(createCell("0.00"));

                    BigDecimal shortageQuantity = quantity.subtract(currentQuantity);
                    BigDecimal shortageSum = sum.subtract(currentSum);

                    table.addCell(createCell(shortageQuantity.toString()));
                    table.addCell(createCell(shortageSum.toString()).setBorderRight(new SolidBorder(borderWidth)));

                    quantityAndSum.set(6, quantityAndSum.get(6).add(shortageQuantity));
                    quantityAndSum.set(7, quantityAndSum.get(7).add(shortageSum));
                } else {
                    table.addCell(createCell("0.000"));
                    table.addCell(createCell("0.00"));
                    table.addCell(createCell("0.000"));
                    table.addCell(createCell("0.00").setBorderRight(new SolidBorder(borderWidth)));
                }
            });

            if (pageNumber % 5 == 0)
                table.flush();
        } while (pageNumber < totalPages);

        List<Inventory> extraInventory = inventoryRepository.findExtraInventory(predicate);
        if (!extraInventory.isEmpty()) {
            extraInventory.forEach(i -> {
                addEmptyCellNTimes(1, 2, table, font, true);
                addEmptyCellNTimes(1, 7, table, font);
                table.addCell(createCell(i.getInventoryNumber()));
                addEmptyCellNTimes(1, 3, table, font);
                addEmptyCellNTimes(1, 2, table, font);
                addEmptyCellNTimes(3, 3, table, font);
                table.addCell(createCell(i.getCurrentQuantity().toString(), 1, font));
                quantityAndSum.set(2, i.getCurrentQuantity());
                addEmptyCellNTimes(4, 3, table, font);
                addEmptyCellNTimes(1, 3, table, font, false);
            });
        }

        table.addCell(createCell("Total", 1, 6, bold, true).setBorder(new SolidBorder(borderWidth)));
        for (int i = 0; i < 8; i++) {
            table.addCell(createCell(quantityAndSum.get(i).toString(),1, 1, bold, true)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(borderWidth)));
        }

        table.addCell(createCell("Total items", 1, 6, bold, true).setBorder(new SolidBorder(borderWidth)));
        table.addCell(createCell(String.valueOf(totalElements), 1, 8, bold, true)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(borderWidth)));

        table.complete();
        document.close();

        return new UrlResource(pathToFile.toUri());
    }

    @SneakyThrows
    private Object[] prepareDocument() {
        Path pathToFile = Files.createTempFile(null, ".pdf");
        PdfWriter writer = new PdfWriter(pathToFile.toFile());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(20, 20, 20, 20);
        font = PdfFontFactory.createFont("src/main/resources/font/Roboto-Regular.ttf", PdfEncodings.IDENTITY_H);
        bold = PdfFontFactory.createFont("src/main/resources/font/Roboto-Bold.ttf", PdfEncodings.IDENTITY_H);
        return new Object[]{pathToFile, document};
    }

    private Cell createCell(String text) {
        return createCell(text, 1, 1, font, false);
    }

    private Cell createCell(String text, int colspan, PdfFont font) {
        return createCell(text, 1, colspan, font, false);
    }

    private Cell createCell(String text, int rowspan, int colspan, PdfFont font, boolean isHeaderOrFooter) {
        return new Cell(rowspan, colspan)
                .add(new Paragraph(text).setFont(font).setFontSize(fontSize).setMultipliedLeading(1.1F))
                .setMinHeight(isHeaderOrFooter ? 10 : 20);
    }

    private void addEmptyCellNTimes(int n, int size, Table table, PdfFont font) {
        addEmptyCellNTimes(n, size, table, font, null);
    }

    private void addEmptyCellNTimes(int n, int size, Table table, PdfFont font, Boolean isLeft) {
        for (int i = 0; i < n; i++) {
            Cell cell = new Cell().add(new Paragraph("-".repeat((int) (size * 6.9)))
                    .setFont(font).setFontSize(fontSize)).setTextAlignment(TextAlignment.CENTER);

            if (isLeft == null)
                table.addCell(cell);
            else if (isLeft)
                table.addCell(cell.setBorderLeft(new SolidBorder(borderWidth)));
            else
                table.addCell(cell.setBorderRight(new SolidBorder(borderWidth)));
        }
    }
}
