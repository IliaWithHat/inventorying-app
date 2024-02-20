package org.ilia.inventoryingapp.http.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.OptionsForIsOwnedByEmployee;
import org.ilia.inventoryingapp.database.entity.Unit;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.filter.ItemFilterForAdmin;
import org.ilia.inventoryingapp.filter.TimeDurationEnum;
import org.ilia.inventoryingapp.service.ItemSequenceService;
import org.ilia.inventoryingapp.service.ItemService;
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

@RequiredArgsConstructor
@RequestMapping("/items")
@Controller
@SessionAttributes("itemFilterForAdmin")
public class ItemController {

    private final ItemService itemService;
    private final ItemSequenceService itemSequenceService;

    @GetMapping
    public String createItemPage(@AuthenticationPrincipal UserDetails userDetails,
                                 ItemDto itemDto,
                                 SaveField saveField,
                                 Model model) {
        Page<ItemDto> itemDtoPage = itemService.findLastFiveItems(userDetails);
        model.addAttribute("items", PageResponse.of(itemDtoPage));
        model.addAttribute("itemDto", itemDto);
        model.addAttribute("saveField", saveField);
        model.addAttribute("units", Unit.values());
        model.addAttribute("currval", itemSequenceService.currval(userDetails));
        return "item/items";
    }

    @GetMapping("/filter")
    public String filterItems(@AuthenticationPrincipal UserDetails userDetails,
                              ItemFilterForAdmin itemFilterForAdmin,
                              @RequestParam(defaultValue = "0") Integer page,
                              Model model) {
        Page<ItemDto> itemDtoPage = itemService.findAll(userDetails, itemFilterForAdmin, page);
        model.addAttribute("items", PageResponse.of(itemDtoPage));
        model.addAttribute("itemFilterForAdmin", itemFilterForAdmin);
        model.addAttribute("optionsForIsOwnedByEmployee", OptionsForIsOwnedByEmployee.values());
        model.addAttribute("optionsForShowItemCreated", TimeDurationEnum.values());
        return "item/filter";
    }

    @DeleteMapping("/filter/clear")
    public String clearFilter(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/items/filter";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal UserDetails userDetails,
                         @Validated ItemDto itemDto,
                         BindingResult bindingResult,
                         SaveField saveField,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("itemDto", itemDto);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
        } else {
            itemService.create(userDetails, itemDto);
            ItemDto itemDtoOnlySavedField = itemService.saveStateOfFields(itemDto, saveField);
            redirectAttributes.addFlashAttribute("itemDto", itemDtoOnlySavedField);
        }
        redirectAttributes.addFlashAttribute("saveField", saveField);
        return "redirect:/items";
    }

    @GetMapping("/{id}")
    public String findById(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable Long id,
                           Model model) {
        ItemDto itemDto = itemService.findById(id, userDetails)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("itemDto", itemDto);
        model.addAttribute("units", Unit.values());
        return "item/item";
    }

    @PutMapping("/{id}")
    public String update(@Validated ItemDto itemDto,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
        } else {
            itemService.update(itemDto, id, userDetails)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            redirectAttributes.addFlashAttribute("saved", "Item successfully updated!!!");
        }
        return "redirect:/items/{id}";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long id) {
        if (!itemService.delete(id, userDetails))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return "<script>window.close();</script>";
    }

    @GetMapping("/export")
    @ResponseBody
    public ResponseEntity<Resource> exportPdf(@AuthenticationPrincipal UserDetails userDetails,
                                              ItemFilterForAdmin itemFilterForAdmin) {
        Resource file = itemService.getPdf(itemFilterForAdmin, userDetails);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"table.pdf\"")
                .body(file);
    }
}
