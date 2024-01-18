package org.ilia.inventoryingapp.http.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.dto.InventoryDto;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.ilia.inventoryingapp.service.InventoryService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventory")
@SessionAttributes({"itemFilter", "firstTime"})
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/blind")
    public String blindInventory(@AuthenticationPrincipal UserDetails userDetails,
                                 InventoryDto inventoryDto,
                                 Model model) {
        Object firstTime = model.getAttribute("firstTime");
        if (firstTime == null) {
            model.addAttribute("firstTime", new Object());
            inventoryService.deleteInventoryByUserDetails(userDetails);
        }
        model.addAttribute("inventoryDto", inventoryDto);
        return "inventory/blind";
    }

    @PostMapping("/blind")
    public String create(@AuthenticationPrincipal UserDetails userDetails,
                         @Validated InventoryDto inventoryDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("inventoryDto", inventoryDto);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
        } else {
            inventoryService.create(userDetails, inventoryDto);
        }
        return "redirect:/inventory/blind";
    }

    @DeleteMapping("/cancel")
    public String cancelInventory(@AuthenticationPrincipal UserDetails userDetails,
                                  SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        inventoryService.deleteInventoryByUserDetails(userDetails);
        return "redirect:/items/filter";
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportResults(@AuthenticationPrincipal UserDetails userDetails,
                                                  ItemFilter itemFilter,
                                                  SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        inventoryService.deleteInventoryByUserDetails(userDetails);

        Resource file = inventoryService.generateFullPdfByItemFilterAndUserDetails(itemFilter, userDetails);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"table.pdf\"").body(file);
    }
}
