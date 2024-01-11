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
        model.addAttribute("nextSerialNumber", itemDtoPage.getContent().getFirst().getSerialNumber() + 1);
        return "item/items";
    }

    @GetMapping("/filter")
    public String filterItems(@AuthenticationPrincipal UserDetails userDetails,
                              Model model,
                              Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("serialNumber"));
        Page<ItemDto> itemDtoPage = itemService.findAll(userDetails, pageable);
        model.addAttribute("items", PageResponse.of(itemDtoPage));
        return "item/filter";
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id) {
        return itemService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public String create(@ModelAttribute("itemDto") @Validated ItemDto itemDto,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("itemDto", itemDto);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
        } else {
            itemService.create(userDetails, itemDto);
            ItemDto saveStoredInString = ItemDto.builder()
                    .storedIn(itemDto.getStoredIn())
                    .build();
            redirectAttributes.addFlashAttribute("itemDto", saveStoredInString);
        }
        return "redirect:/items";
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long id) {
        return itemService.update(itemDto, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        if (!itemService.delete(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        TODO use JavaScript for deleting row.
        return "redirect:/items";
    }
}
