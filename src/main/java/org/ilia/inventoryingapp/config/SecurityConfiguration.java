package org.ilia.inventoryingapp.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.ilia.inventoryingapp.database.entity.Role.ADMIN;
import static org.ilia.inventoryingapp.database.entity.Role.USER;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(Customizer.withDefaults());
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/", "/login", "/registration").permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/login/redirect", "items/filter", "items/export", "inventory/**").hasAnyAuthority(ADMIN.getAuthority(), USER.getAuthority())
                .requestMatchers("/items/**", "/admin/users/**").hasAuthority(ADMIN.getAuthority())
                .anyRequest().denyAll());
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID"));
        http.formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/login/redirect", true));
        http.sessionManagement(sm -> sm
                .maximumSessions(1)
                .expiredUrl("/login")
                .sessionRegistry(sessionRegistry()));
        return http.build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
