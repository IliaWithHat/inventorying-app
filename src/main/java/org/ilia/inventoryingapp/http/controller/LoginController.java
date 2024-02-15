package org.ilia.inventoryingapp.http.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.ilia.inventoryingapp.database.entity.Role.ADMIN;

@Controller
@RequestMapping("login")
public class LoginController {

    @GetMapping
    public String login() {
        return "user/login";
    }

    @GetMapping("/redirect")
    public String redirectAfterLogin(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails.getAuthorities().contains(ADMIN) ? "redirect:/items" : "redirect:/items/filter";
    }
}
