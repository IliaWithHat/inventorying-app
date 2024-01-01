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
	}
}
