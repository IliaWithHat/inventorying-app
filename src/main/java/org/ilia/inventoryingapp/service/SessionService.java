package org.ilia.inventoryingapp.service;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.User;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRegistry sessionRegistry;

    public void expireSession(User user) {
        expireSession(List.of(user));
    }

    public void expireSession(List<User> users) {
        List<String> userEmails = users.stream()
                .map(User::getEmail)
                .toList();
        sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> userEmails.contains(((UserDetails) principal).getUsername()))
                .flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
                .forEach(SessionInformation::expireNow);
    }
}
