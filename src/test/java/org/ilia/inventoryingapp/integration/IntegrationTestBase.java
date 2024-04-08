package org.ilia.inventoryingapp.integration;

import org.ilia.inventoryingapp.database.entity.*;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql({"/sql/init.sql"})
public abstract class IntegrationTestBase {

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:16.1");

    protected static User user = new User(1, "test", "{noop}1234", "Ilia", "Rozhko", "+380-96-873-77-76", Role.ADMIN, null);

    protected static UserDetails userDetails = new UserDetailsImpl(user);

    protected static List<Item> items = List.of(
            new Item(1L, 1L, "Chair", "00000001", "Room 1", Unit.PC, new BigDecimal("1.000"), new BigDecimal("50.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(2L, 2L, "Chair", "00000002", "Room 1", Unit.PC, new BigDecimal("1.000"), new BigDecimal("50.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(3L, 3L, "Chair", "00000003", "Room 1", Unit.PC, new BigDecimal("1.000"), new BigDecimal("50.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(4L, 4L, "Chair", "00000004", "Room 1", Unit.PC, new BigDecimal("1.000"), new BigDecimal("50.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(5L, 5L, "Chair", "00000005", "Room 1", Unit.PC, new BigDecimal("1.000"), new BigDecimal("50.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(6L, 6L, "Chair", "00000006", "Room 1", Unit.PC, new BigDecimal("1.000"), new BigDecimal("50.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(7L, 7L, "Table", "00000007", "Room 1", Unit.PC, new BigDecimal("1.000"), new BigDecimal("75.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(8L, 8L, "Table", "00000008", "Room 1", Unit.PC, new BigDecimal("1.000"), new BigDecimal("75.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(9L, 9L, "Closet", "00000009", "Room 1", Unit.PC, new BigDecimal("1.000"), new BigDecimal("120.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(10L, 10L, "Wire", "00000010", "Warehouse", Unit.M, new BigDecimal("5.100"), new BigDecimal("20.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(11L, 11L, "Cable", "00000011", "Warehouse", Unit.M, new BigDecimal("8.550"), new BigDecimal("70.12"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(12L, 12L, "Net", "00000012", "Warehouse", Unit.M2, new BigDecimal("0.400"), new BigDecimal("0.01"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(13L, 13L, "Glasses", "00000013", "Warehouse", Unit.PC, new BigDecimal("1.000"), new BigDecimal("15.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(14L, 14L, "Engine", "00000014", "Warehouse", Unit.PC, new BigDecimal("1.000"), new BigDecimal("3000.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(15L, 15L, "Thermometer", "00000015", "Warehouse", Unit.PC, new BigDecimal("10.000"), new BigDecimal("13.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(16L, 16L, "Boots", "00000016", "Warehouse", Unit.PAIR, new BigDecimal("5.000"), new BigDecimal("1230.33"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(17L, 17L, "Wallpaper white", "00000017", "Warehouse", Unit.ROLL, new BigDecimal("34.000"), new BigDecimal("32.32"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(18L, 18L, "Wallpaper black", "00000018", "Warehouse", Unit.ROLL, new BigDecimal("22.000"), new BigDecimal("92.88"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(19L, 19L, "Wallpaper blue", "00000019", "Warehouse", Unit.ROLL, new BigDecimal("76.000"), new BigDecimal("10.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(20L, 20L, "Sand", "00000020", "Warehouse", Unit.T, new BigDecimal("3.322"), new BigDecimal("939.05"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(21L, 21L, "Calendar", "000001-01", "Room 2", Unit.PC, new BigDecimal("1.000"), new BigDecimal("0.00"), true, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user)
    );


    @BeforeAll
    static void runContainer() {
        container.start();
    }

    @BeforeAll
    static void manuallyAuthenticate() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
    }
}
