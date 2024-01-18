package org.ilia.inventoryingapp.http.controller;

import org.ilia.inventoryingapp.filter.ItemFilter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/inventory")
@SessionAttributes("itemFilter")
public class InventoryController {

    @GetMapping("/blind")
    public String blindInventory(@AuthenticationPrincipal UserDetails userDetails) {
        return "inventory/blind";
    }

    @PostMapping("/blind")
    public String create(@AuthenticationPrincipal UserDetails userDetails) {
        return "redirect:/inventory/blind";
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportResults(@AuthenticationPrincipal UserDetails userDetails,
                                                  ItemFilter itemFilter) {
        Resource file = null;

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"table.pdf\"").body(file);
    }
}
