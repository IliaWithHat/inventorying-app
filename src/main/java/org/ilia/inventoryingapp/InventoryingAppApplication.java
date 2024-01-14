package org.ilia.inventoryingapp;

import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.service.ItemService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@SpringBootApplication
public class InventoryingAppApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(InventoryingAppApplication.class, args);
		log.info("Done");

//		ItemRepository itemRepository = context.getBean(ItemRepository.class);
//		for (int i = 10000; i < 20000; i++) {
//			itemRepository.save(Item.builder()
//							.serialNumber((long) i)
//							.inventoryNumber(String.valueOf(i))
//							.name("Second sample")
//							.storedIn("Second room")
//							.quantity(2)
//							.isOwnedByEmployee(false)
//							.additionalInfo("This second part of 10000 table")
//							.createdAt(LocalDateTime.now())
//							.createdBy(User.builder().id(1).build())
//					.build());
//		}
//		log.info("Write");
	}
}
