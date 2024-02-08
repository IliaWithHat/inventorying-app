package org.ilia.inventoryingapp.http.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.dto.InventoryDto;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.filter.ItemFilterForAdmin;
import org.ilia.inventoryingapp.service.InventoryService;
import org.ilia.inventoryingapp.viewUtils.PageResponse;
import org.ilia.inventoryingapp.viewUtils.SaveField;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventory")
@SessionAttributes({"itemFilterForAdmin"})
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/blind")
    public String blindInventory(@AuthenticationPrincipal UserDetails userDetails,
                                 @ModelAttribute("returnTo") String returnTo,
                                 InventoryDto inventoryDto,
                                 SaveField saveField,
                                 Model model) {
        if (!"blind".equals(returnTo))
            inventoryService.deleteInventory(userDetails);
        model.addAttribute("inventoryDto", inventoryDto);
        model.addAttribute("saveField", saveField);
        return "inventory/blind";
    }

    @GetMapping("/sighted")
    public String sightedInventory(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @ModelAttribute("returnTo") String returnTo,
                                   ItemFilterForAdmin itemFilterForAdmin,
                                   Model model) {
        if (!"sighted".equals(returnTo))
            inventoryService.deleteInventory(userDetails);
        Page<ItemDto> items = inventoryService.findAll(userDetails, itemFilterForAdmin, page);
        model.addAttribute("items", PageResponse.of(items));
        return "inventory/sighted";
    }

    @PostMapping
    public String create(@RequestParam(defaultValue = "0") Integer page,
                         RedirectAttributes redirectAttributes,
                         @Validated InventoryDto inventoryDto,
                         BindingResult bindingResult,
                         SaveField saveField,
                         String returnTo) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("inventoryDto", inventoryDto);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
        } else {
            inventoryService.create(inventoryDto);

            if (!saveField.equals(new SaveField())) {
                InventoryDto inventoryDtoWithSavedFields = inventoryService.saveStateOfFields(inventoryDto, saveField);
                redirectAttributes.addFlashAttribute(inventoryDtoWithSavedFields);
            }
        }
        redirectAttributes.addFlashAttribute("saveField", saveField);
        redirectAttributes.addFlashAttribute("returnTo", returnTo);

        if ("sighted".equals(returnTo))
            return "redirect:/inventory/sighted?page=" + page;
        else if ("blind".equals(returnTo))
            return "redirect:/inventory/blind";

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
                                                  SessionStatus sessionStatus,
                                                  String inventoryMethod,
                                                  ItemFilterForAdmin itemFilterForAdmin) {
        Resource file = inventoryService.getPdf(itemFilterForAdmin, userDetails, inventoryMethod);

        sessionStatus.setComplete();
        inventoryService.deleteInventory(userDetails);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        //TODO after sending file make redirect to /items/filter
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"table.pdf\"").body(file);
    }
}
