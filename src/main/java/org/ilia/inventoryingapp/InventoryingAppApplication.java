package org.ilia.inventoryingapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class InventoryingAppApplication {

	public static void main(String[] args) {
		var context = SpringApplication.run(InventoryingAppApplication.class, args);
		log.info("Done");
//		ItemRepository itemRepository = context.getBean(ItemRepository.class);
//		for (int i = 1; i <= 100; i++) {
//			Item item = Item.builder()
//					.serialNumber((long) i)
//					.inventoryNumber((long) i)
//					.name("Same name")
//					.storedIn("Main room")
//					.quantity(1)
//					.isOwnedByEmployee(true)
//					.createdAt(LocalDateTime.now())
//					.createdBy(User.builder().id(4).build())
//					.build();
//			itemRepository.save(item);
//		}
	}
}
