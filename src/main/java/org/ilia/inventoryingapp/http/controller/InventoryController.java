package org.ilia.inventoryingapp.http.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.dto.InventoryDto;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.ilia.inventoryingapp.pdf.GeneratePdf;
import org.ilia.inventoryingapp.service.InventoryService;
import org.ilia.inventoryingapp.viewUtils.PageResponse;
import org.ilia.inventoryingapp.viewUtils.SaveField;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
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
    private final GeneratePdf generatePdf;

    @GetMapping("/blind")
    public String blindInventory(@AuthenticationPrincipal UserDetails userDetails,
                                 InventoryDto inventoryDto,
                                 SaveField saveField,
                                 Model model) {
        inventoryService.clearTableInventoryBeforeStartInventorying(userDetails, model);
        model.addAttribute("inventoryDto", inventoryDto);
        model.addAttribute("saveField", saveField);
        return "inventory/blind";
    }

    @GetMapping("/sighted")
    public String sightedInventory(@AuthenticationPrincipal UserDetails userDetails,
                                   Model model,
                                   ItemFilter itemFilter,
                                   @RequestParam(defaultValue = "0") Integer page) {
        inventoryService.clearTableInventoryBeforeStartInventorying(userDetails, model);
        Page<ItemDto> items = inventoryService.findAll(userDetails, itemFilter, page);
        model.addAttribute("items", PageResponse.of(items));
        return "inventory/sighted";
    }

    @PostMapping
    public String create(String returnTo,
                         @RequestParam(defaultValue = "0") Integer page,
                         @Validated InventoryDto inventoryDto,
                         BindingResult bindingResult,
                         SaveField saveField,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("inventoryDto", inventoryDto);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
        } else {
            inventoryService.create(inventoryDto);
            InventoryDto inventoryDtoWithSavedFields = inventoryService.saveStateOfFields(inventoryDto, saveField);
            redirectAttributes.addFlashAttribute(inventoryDtoWithSavedFields);
        }
        redirectAttributes.addFlashAttribute(saveField);
        if ("sighted".equals(returnTo))
            return String.format("redirect:/inventory/sighted?page=%d", page);
        return "redirect:/inventory/blind";
    }

    @DeleteMapping("/cancel")
    public String cancelInventory(@AuthenticationPrincipal UserDetails userDetails,
                                  SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        inventoryService.deleteInventory(userDetails);
        return "redirect:/items/filter";
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportResults(@AuthenticationPrincipal UserDetails userDetails,
                                                  ItemFilter itemFilter,
                                                  SessionStatus sessionStatus) {
        Resource file = generatePdf.generateInventoryPdf(itemFilter, userDetails);

        sessionStatus.setComplete();
        inventoryService.deleteInventory(userDetails);

        if (file == null)
            return ResponseEntity.notFound().build();

        //TODO after sending file make redirect to /items/filter
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"table.pdf\"").body(file);
    }
}
