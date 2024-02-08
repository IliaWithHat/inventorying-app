package org.ilia.inventoryingapp.http.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.dto.ItemFilterDto;
import org.ilia.inventoryingapp.service.ItemFilterService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
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
            itemFilterService.saveOrUpdate(id, userDetails, itemFilterDto);
            redirectAttributes.addFlashAttribute("savedItemFilter", "Filter set successfully!!!");
        }
        return "redirect:/admin/users/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Integer id) {
        itemFilterService.delete(id, userDetails);
        return "redirect:/admin/users/{id}";
    }
}
