package org.ilia.inventoryingapp.http.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.dto.PageResponse;
import org.ilia.inventoryingapp.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

@RequiredArgsConstructor
@RequestMapping("/items")
@Controller
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public String getFirstFiveItems(@ModelAttribute("itemDto") ItemDto itemDto,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("serialNumber").descending());
        Page<ItemDto> itemDtoPage = itemService.findAll(userDetails, pageable);
        model.addAttribute("items", PageResponse.of(itemDtoPage));
//        model.addAttribute("nextSerialNumber", itemDtoPage.getContent().getFirst().getSerialNumber() + 1);
        model.addAttribute("itemDto", itemDto);
        return "item/items";
    }

    @GetMapping("/filter")
    public String filterItems(@AuthenticationPrincipal UserDetails userDetails,
                              Model model,
                              @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("serialNumber"));
        Page<ItemDto> itemDtoPage = itemService.findAll(userDetails, pageable);
        model.addAttribute("items", PageResponse.of(itemDtoPage));
        return "item/filter";
    }

    @PostMapping
    public String create(@Validated ItemDto itemDto,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("itemDto", itemDto);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
        } else {
            itemService.create(userDetails, itemDto);
            //TODO add checkbox "Save state of field"
            ItemDto savedFields = ItemDto.builder()
                    .inventoryNumber(itemDto.getInventoryNumber() + 1)
                    .storedIn(itemDto.getStoredIn())
                    .quantity(itemDto.getQuantity())
                    .isOwnedByEmployee(itemDto.getIsOwnedByEmployee())
                    .build();
            redirectAttributes.addFlashAttribute("itemDto", savedFields);
        }
        return "redirect:/items";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        ItemDto itemDto = itemService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
        itemService.update(itemDto, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        redirectAttributes.addFlashAttribute("saved", "Item successfully updated!!!");
        return "redirect:/items/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, String returnTo) {
        if (!itemService.delete(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if ("/filter".equals(returnTo))
            return "redirect:/items" + returnTo;
        return "redirect:/items";
    }
}
