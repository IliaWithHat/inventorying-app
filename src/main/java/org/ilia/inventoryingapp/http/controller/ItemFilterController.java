package org.ilia.inventoryingapp.http.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.dto.ItemFilterDto;
import org.ilia.inventoryingapp.exception.UserNotFoundException;
import org.ilia.inventoryingapp.service.ItemFilterService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/filter")
public class ItemFilterController {

    private final ItemFilterService itemFilterService;

    @PostMapping("/{id}")
    public String setUp(@AuthenticationPrincipal UserDetails userDetails,
                        @Validated ItemFilterDto itemFilterDto,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        @PathVariable Integer id) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("filterErrors", bindingResult.getAllErrors());
        } else {
            try {
                itemFilterService.saveOrUpdate(id, userDetails, itemFilterDto);
            } catch (UserNotFoundException e) {
                log.warn("User id: {} don't belong current user: {}", e.getId(), e.getUserDetails());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            redirectAttributes.addFlashAttribute("savedItemFilter", "Filter set successfully!!!");
        }
        return "redirect:/admin/users/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Integer id) {
        try {
            itemFilterService.delete(id, userDetails);
        } catch (UserNotFoundException e) {
            log.warn("User id: {} don't belong current user: {}", e.getId(), e.getUserDetails());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/admin/users/{id}";
    }
}
