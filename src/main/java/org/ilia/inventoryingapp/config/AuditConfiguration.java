package org.ilia.inventoryingapp.config;

import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfiguration {

    @Bean
    public AuditorAware<User> auditorAware() {
//        SecurityContext.getCurrentUser().getId();
        return () -> Optional.of(User.builder().id(1).build());
    }
}
