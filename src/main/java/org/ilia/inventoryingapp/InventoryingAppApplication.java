package org.ilia.inventoryingapp;

import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.Unit;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@SpringBootApplication
public class InventoryingAppApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(InventoryingAppApplication.class, args);
        log.info("Done");
        ItemRepository itemRepository = context.getBean(ItemRepository.class);
        User user = User.builder().id(4).build();
        for (int i = 0; i < 1_000; i++) {
            Item item = Item.builder()
                    .serialNumber(191264L + i)
                    .name("Object")
                    .inventoryNumber(String.valueOf(32837 + i))
                    .storedIn("Heap")
                    .unit(Unit.PC)
                    .quantity(new BigDecimal(1))
                    .pricePerUnit(new BigDecimal(192.288))
                    .isOwnedByEmployee(false)
                    .createdAt(LocalDateTime.now())
                    .createdBy(user)
                    .build();
            itemRepository.save(item);
        }
        log.info("Created all");
    }
}
