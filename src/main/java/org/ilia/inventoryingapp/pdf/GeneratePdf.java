package org.ilia.inventoryingapp.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.querydsl.PredicateBuilder;
import org.ilia.inventoryingapp.database.repository.InventoryRepository;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.filter.ItemFilterForAdmin;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class GeneratePdf {

    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final PredicateBuilder predicateBuilder;
    private static final int FONT_SIZE = 8;
    private static final float BORDER_WIDTH = 1.2F;
    private PdfFont regularFont;
    private PdfFont boldFont;

    @SneakyThrows
    private Pair<Path, Document> prepareDocument() {
        Path pathToFile = Files.createTempFile(null, ".pdf");
        PdfWriter writer = new PdfWriter(pathToFile.toFile());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(20, 20, 20, 20);
        regularFont = PdfFontFactory.createFont(getFontFilePath("Roboto-Regular.ttf"), PdfEncodings.IDENTITY_H);
        boldFont = PdfFontFactory.createFont(getFontFilePath("Roboto-Bold.ttf"), PdfEncodings.IDENTITY_H);
        return Pair.of(pathToFile, document);
    }

    private String getFontFilePath(String fontName) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "src/main/resources/font/" + fontName;
        } else {
            return "classpath:font/" + fontName;
        }
    }

    private List<BigDecimal> initializeList(int capacity) {
        List<BigDecimal> list = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            list.add(new BigDecimal(i % 2 == 0 ? "0.000" : "0.00"));
        }
        return list;
    }

    @SneakyThrows
    public Resource generateStandardPdf(ItemFilterForAdmin itemFilterForAdmin, User user) {
        Pair<Path, Document> pathAndDocument = prepareDocument();
        Path pathToFile = pathAndDocument.getFirst();
        Document document = pathAndDocument.getSecond();

        int totalPages = 0;
        int pageNumber = 0;
        long totalElements = 0;

        List<BigDecimal> quantityAndSum = initializeList(2);
        List<BigDecimal> totalQuantityAndSum = initializeList(2);

        Predicate predicate = predicateBuilder.buildPredicate(user, itemFilterForAdmin);
        do {
            Table table = createStandardTableAndHeader();

            Pageable pageable = PageRequest.of(pageNumber, 20, Sort.by("serialNumber"));
            Page<Item> items = itemRepository.findAll(predicate, pageable);
            if (pageNumber == 0) {
                totalPages = items.getTotalPages();
                totalElements = items.getTotalElements();
            }
            pageNumber++;

            items.forEach(item -> addDataToStandardTable(table, item, quantityAndSum));

            for (int i = 0; i < 2; i++) {
                totalQuantityAndSum.set(i, totalQuantityAndSum.get(i).add(quantityAndSum.get(i)));
            }

            if (pageNumber == totalPages && items.getNumberOfElements() < 20) {
                addEmptyRows(table, 9, 20, items.getNumberOfElements());
            }

            createStandardFooter(table, quantityAndSum, pageNumber, items.getNumberOfElements());

            document.add(table);
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        } while (pageNumber < totalPages);

        Table table = createStandardTableAndHeader();
        createStandardFooter(table, totalQuantityAndSum, totalElements);

        document.add(table);

        String text = String.format("The export was made on %1$tF %1$tR by %2$s %3$s.",
                LocalDateTime.now(), user.getFirstName(), user.getLastName());
        document.add(new Paragraph(text).setFont(regularFont).setFontSize(FONT_SIZE + 2));

        document.close();

        return new UrlResource(pathToFile.toUri());
    }

    private Table createStandardTableAndHeader() {
        Table table = new Table(new float[]{2, 7, 3, 3, 1, 3, 3, 3, 3})
                .setFixedLayout()
                .setWidth(UnitValue.createPercentValue(100));

        List.of("Serial number", "Item name", "Inventory number", "Stored in", "Unit", "Price per unit", "Quantity", "Sum", "Owned by employee")
                .forEach(s -> table
                        .addHeaderCell(createCell(s, 1, boldFont, true)
                                .setBorder(new SolidBorder(BORDER_WIDTH))
                                .setTextAlignment(TextAlignment.CENTER)));
        return table;
    }

    private void addDataToStandardTable(Table table, Item item, List<BigDecimal> quantityAndSum) {
        addDataToStandardTable(table, item, null, quantityAndSum);
    }

    private void addDataToStandardTable(Table table, Tuple tuple, List<BigDecimal> quantityAndSum) {
        Item item = tuple.get(0, Item.class);
        Inventory inventory = tuple.get(1, Inventory.class);
        addDataToStandardTable(table, item, inventory, quantityAndSum);
    }

    private void addDataToStandardTable(Table table, Item item, Inventory inventory, List<BigDecimal> quantityAndSum) {
        BigDecimal sum;
        if (inventory == null) {
            sum = item.getQuantity().multiply(item.getPricePerUnit()).setScale(2, RoundingMode.HALF_UP);
        } else {
            sum = inventory.getCurrentQuantity().multiply(item.getPricePerUnit()).setScale(2, RoundingMode.HALF_UP);
        }

        table.addCell(createCell(item.getSerialNumber().toString(), true));
        table.addCell(createCell(item.getName()));
        table.addCell(createCell(item.getInventoryNumber()));
        table.addCell(createCell(item.getStoredIn()));
        table.addCell(createCell(item.getUnit().toString()));
        table.addCell(createCell(item.getPricePerUnit().toString()));

        if (inventory == null) {
            table.addCell(createCell(item.getQuantity().toString()));
        } else {
            table.addCell(createCell(inventory.getCurrentQuantity().toString()));
        }

        table.addCell(createCell(sum.toString()));
        table.addCell(createCell(item.getIsOwnedByEmployee() ? "Yes" : "No", false));

        if (inventory == null) {
            quantityAndSum.set(0, quantityAndSum.get(0).add(item.getQuantity()));
        } else {
            quantityAndSum.set(0, quantityAndSum.get(0).add(inventory.getCurrentQuantity()));
        }
        quantityAndSum.set(1, quantityAndSum.get(1).add(sum));
    }

    private void createStandardFooter(Table table, List<BigDecimal> numbers, int pageNumber, int numberOfElements) {
        createStandardFooter(table, numbers, false, pageNumber, numberOfElements, 0);
    }

    private void createStandardFooter(Table table, List<BigDecimal> numbers, long totalElements) {
        createStandardFooter(table, numbers, true, 0, 0, totalElements);
    }

    private void createStandardFooter(Table table, List<BigDecimal> numbers, boolean isFinal, int pageNumber, int numberOfElements, long totalElements) {
        table.addCell(createCell(isFinal ? "Total" : "Total for page " + pageNumber, 6, boldFont, true)
                .setBorder(new SolidBorder(BORDER_WIDTH)));
        for (int i = 0; i < 2; i++) {
            table.addCell(createCell(numbers.set(i, new BigDecimal(i % 2 == 0 ? "0.000" : "0.00")).toString(), 1, boldFont, true)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(BORDER_WIDTH)));
        }

        table.addCell(createCell("", 1, boldFont, true)
                .setBorder(new SolidBorder(BORDER_WIDTH)));

        table.addCell(createCell(isFinal ? "Total items" : "Total items on page " + pageNumber, 6, boldFont, true)
                .setBorder(new SolidBorder(BORDER_WIDTH)));
        table.addCell(createCell(String.valueOf(isFinal ? totalElements : numberOfElements), 2, boldFont, true)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(BORDER_WIDTH)));

        table.addCell(createCell("", 1, boldFont, true)
                .setBorder(new SolidBorder(BORDER_WIDTH)));
    }

    @SneakyThrows
    public Resource generateInventoryPdf(ItemFilterForAdmin itemFilterForAdmin, User user, String inventoryMethod) {
        Pair<Path, Document> pathAndDocument = prepareDocument();
        Path pathToFile = pathAndDocument.getFirst();
        Document document = pathAndDocument.getSecond();

        int totalPages = 0;
        int pageNumber = 0;
        long totalElements = 0;

        List<BigDecimal> quantityAndSum = initializeList(8);
        List<BigDecimal> totalQuantityAndSum = initializeList(8);

        Predicate predicate = predicateBuilder.buildPredicate(user, itemFilterForAdmin);
        do {
            Table table = createInventoryTableAndHeader();

            Pageable pageable = PageRequest.of(pageNumber, 20, Sort.by("serialNumber"));
            Page<Tuple> itemsAndInventory = inventoryRepository.findItemsAndInventory(predicate, pageable, user);
            if (pageNumber == 0) {
                totalPages = itemsAndInventory.getTotalPages();
                totalElements = itemsAndInventory.getTotalElements();
            }
            pageNumber++;

            itemsAndInventory.forEach(tuple -> addDataToInventoryTable(table, tuple, quantityAndSum));

            for (int i = 0; i < 8; i++) {
                totalQuantityAndSum.set(i, totalQuantityAndSum.get(i).add(quantityAndSum.get(i)));
            }

            if (pageNumber == totalPages && itemsAndInventory.getNumberOfElements() < 20) {
                addEmptyRows(table, 14, 20, itemsAndInventory.getNumberOfElements());
            }

            createInventoryFooter(table, quantityAndSum, pageNumber, itemsAndInventory.getNumberOfElements());

            document.add(table);
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        } while (pageNumber < totalPages);

        if (inventoryMethod.equals("blind")) {
            Predicate[] predicates = predicateBuilder.buildPredicate(user, itemFilterForAdmin, true);
            List<Tuple> extraInventory = inventoryRepository.findExtraInventory(predicates, user);
            if (!extraInventory.isEmpty()) {
                //TODO add pagination
                totalElements = addExtraInventoryToTheTable(document, extraInventory, totalQuantityAndSum, totalElements);
            }
        }

        Table table = createInventoryTableAndHeader();
        createInventoryFooter(table, totalQuantityAndSum, totalElements);

        document.add(table);

        String text = String.format("The inventory was carried out by %s %s using the \"%s\" method on %4$tF %4$tR.",
                user.getFirstName(), user.getLastName(), inventoryMethod, LocalDateTime.now());
        document.add(new Paragraph(text).setFont(regularFont).setFontSize(FONT_SIZE + 2));

        document.close();

        return new UrlResource(pathToFile.toUri());
    }

    private Table createInventoryTableAndHeader() {
        Table table = new Table(new float[]{2, 7, 4, 3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3})
                .setFixedLayout()
                .setWidth(UnitValue.createPercentValue(100));

        List.of("Serial number", "Item name", "Inventory number", "Stored in", "Unit", "Price per unit")
                .forEach(s -> table.
                        addHeaderCell(createCell(s, 2, 1, boldFont, true, null)
                                .setBorder(new SolidBorder(BORDER_WIDTH))
                                .setTextAlignment(TextAlignment.CENTER)));

        List.of("According to inventory", "Actual availability", "Surplus", "Shortage")
                .forEach(s -> table.
                        addHeaderCell(createCell(s, 2, boldFont, true)
                                .setBorder(new SolidBorder(BORDER_WIDTH))
                                .setTextAlignment(TextAlignment.CENTER)));

        List<String> qAndS = List.of("Quantity", "Sum");
        for (int i = 0; i < 8; i++) {
            table.addHeaderCell(createCell(qAndS.get(i % 2 == 0 ? 0 : 1), 1, boldFont, true)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(BORDER_WIDTH)));
        }
        return table;
    }

    private void addDataToInventoryTable(Table table, Tuple tuple, List<BigDecimal> quantityAndSum) {
        Item item = tuple.get(0, Item.class);
        Inventory inventory = tuple.get(1, Inventory.class);

        table.addCell(createCell(item.getSerialNumber().toString(), true));
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
            table.addCell(createCell("0.00", false));
        } else if (quantity.compareTo(currentQuantity) > 0) {
            table.addCell(createCell("0.000"));
            table.addCell(createCell("0.00"));

            BigDecimal shortageQuantity = quantity.subtract(currentQuantity);
            BigDecimal shortageSum = sum.subtract(currentSum);

            table.addCell(createCell(shortageQuantity.toString()));
            table.addCell(createCell(shortageSum.toString(), false));

            quantityAndSum.set(6, quantityAndSum.get(6).add(shortageQuantity));
            quantityAndSum.set(7, quantityAndSum.get(7).add(shortageSum));
        } else {
            table.addCell(createCell("0.000"));
            table.addCell(createCell("0.00"));
            table.addCell(createCell("0.000"));
            table.addCell(createCell("0.00", false));
        }
    }

    private long addExtraInventoryToTheTable(Document document, List<Tuple> extraInventory, List<BigDecimal> totalQuantityAndSum, long totalElements) {
        document.add(new Paragraph("Items that shouldn't be here")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER));

        List<BigDecimal> extraQuantityAndSum = initializeList(2);

        Table table = createStandardTableAndHeader();
        extraInventory.forEach(tuple -> addDataToStandardTable(table, tuple, extraQuantityAndSum));

        totalQuantityAndSum.set(2, totalQuantityAndSum.get(2).add(extraQuantityAndSum.get(0)));
        totalQuantityAndSum.set(3, totalQuantityAndSum.get(3).add(extraQuantityAndSum.get(1)));

        totalQuantityAndSum.set(4, totalQuantityAndSum.get(4).add(extraQuantityAndSum.get(0)));
        totalQuantityAndSum.set(5, totalQuantityAndSum.get(5).add(extraQuantityAndSum.get(1)));

        totalElements += extraInventory.size();

        createStandardFooter(table, extraQuantityAndSum, extraInventory.size());

        document.add(table);
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        return totalElements;
    }

    private void createInventoryFooter(Table table, List<BigDecimal> numbers, int pageNumber, int numberOfElements) {
        createInventoryFooter(table, numbers, false, pageNumber, numberOfElements, 0);
    }

    private void createInventoryFooter(Table table, List<BigDecimal> numbers, long totalElements) {
        createInventoryFooter(table, numbers, true, 0, 0, totalElements);
    }

    private void createInventoryFooter(Table table, List<BigDecimal> numbers, boolean isFinal, int pageNumber, int numberOfElements, long totalElements) {
        table.addCell(createCell(isFinal ? "Total" : "Total for page " + pageNumber, 6, boldFont, true)
                .setBorder(new SolidBorder(BORDER_WIDTH)));
        for (int i = 0; i < 8; i++) {
            table.addCell(createCell(numbers.set(i, new BigDecimal(i % 2 == 0 ? "0.000" : "0.00")).toString(), 1, boldFont, true)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(BORDER_WIDTH)));
        }

        table.addCell(createCell(isFinal ? "Total items" : "Total items on page " + pageNumber, 6, boldFont, true)
                .setBorder(new SolidBorder(BORDER_WIDTH)));
        table.addCell(createCell(String.valueOf(isFinal ? totalElements : numberOfElements), 8, boldFont, true)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(BORDER_WIDTH)));
    }

    private void addEmptyRows(Table table, int numOfColum, int itemsOnPage, int numberOfElements) {
        for (int i = 0, numOfColumnZeroBased = numOfColum - 1; i < (itemsOnPage - numberOfElements) * numOfColum; i++) {
            if (i % numOfColum == 0) {
                table.addCell(createCell("", true));
            } else if (i % numOfColumnZeroBased == 0) {
                table.addCell(createCell("", false));
                numOfColumnZeroBased += numOfColum;
            } else {
                table.addCell(createCell("", null));
            }
        }
    }

    private Cell createCell(String text) {
        return createCell(text, 1, 1, regularFont, false, null);
    }

    private Cell createCell(String text, Boolean isLeft) {
        return createCell(text, 1, 1, regularFont, false, isLeft);
    }

    private Cell createCell(String text, int colspan, PdfFont font, boolean isHeaderOrFooter) {
        return createCell(text, 1, colspan, font, isHeaderOrFooter, null);
    }

    private Cell createCell(String text, int rowspan, int colspan, PdfFont font, boolean isHeaderOrFooter, Boolean isLeft) {
        Cell cell = new Cell(rowspan, colspan)
                .add(new Paragraph(text).setFont(font).setFontSize(FONT_SIZE).setMultipliedLeading(1.1F))
                .setMinHeight(isHeaderOrFooter ? 10 : 20);
        //TODO setMinHeight() must die. Create method which calculate row height by text.

        if (isLeft == null)
            return cell;

        if (isLeft)
            return cell.setBorderLeft(new SolidBorder(BORDER_WIDTH));
        else
            return cell.setBorderRight(new SolidBorder(BORDER_WIDTH));
    }
}
