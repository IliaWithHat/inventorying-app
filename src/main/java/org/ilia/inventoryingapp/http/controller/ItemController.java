package org.ilia.inventoryingapp.http.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.viewUtils.PageResponse;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.ilia.inventoryingapp.service.ItemService;
import org.ilia.inventoryingapp.viewUtils.SaveField;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/items")
@Controller
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public String getFirstFiveItems(@AuthenticationPrincipal UserDetails userDetails,
                                    ItemDto itemDto,
                                    SaveField saveField,
                                    Model model) {
        Page<ItemDto> itemDtoPage = itemService.findLastFiveItems(userDetails);
        model.addAttribute("items", PageResponse.of(itemDtoPage));
        model.addAttribute("itemDto", itemDto);
        model.addAttribute("saveField", saveField);
        return "item/items";
    }

    @GetMapping("/filter")
    public String filterItems(@AuthenticationPrincipal UserDetails userDetails,
                              ItemFilter itemFilter,
                              @RequestParam(defaultValue = "0") Integer page,
                              Model model) {
        Page<ItemDto> itemDtoPage = itemService.findAll(userDetails, itemFilter, page);
        model.addAttribute("items", PageResponse.of(itemDtoPage));
        model.addAttribute("itemFilter", itemFilter);
        model.addAttribute("optionsForIsOwnedByEmployee", List.of("Ignore", "Yes", "No"));
        model.addAttribute("optionsForShowItemCreated", List.of("Ignore", "1 day", "3 day", "1 week", "2 week", "1 month", "3 month", "6 month", "1 year"));
        return "item/filter";
    }

    @PostMapping
    public String create(@Validated ItemDto itemDto,
                         BindingResult bindingResult,
                         SaveField saveField,
                         @AuthenticationPrincipal UserDetails userDetails,
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
    public String findById(@PathVariable Long id, Model model) {
        ItemDto itemDto = itemService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("itemDto", itemDto);
        return "item/item";
    }

    @PutMapping("/{id}")
    public String update(@Validated ItemDto itemDto,
                         BindingResult bindingResult,
                         @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/items/{id}";
        }
        itemService.update(itemDto, id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        redirectAttributes.addFlashAttribute("saved", "Item successfully updated!!!");
        return "redirect:/items/{id}";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String delete(@PathVariable Long id) {
        //TODO add checkbox to bach delete
        if (!itemService.delete(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return "<script>window.close();</script>";
    }
}
