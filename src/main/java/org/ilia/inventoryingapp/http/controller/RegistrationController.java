package org.ilia.inventoryingapp.http.controller;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.dto.UserDto;
import org.ilia.inventoryingapp.service.UserService;
import org.ilia.inventoryingapp.validation.groups.CreateUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") UserDto user,
                               Model model) {
        model.addAttribute("user", user);
        return "user/registration";
    }

    @PostMapping("/registration")
    public String createUser(@ModelAttribute("user") @Validated({Default.class, CreateUser.class}) UserDto user,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/registration";
        }
        userService.create(null, user);
        return "redirect:/login";
    }
}
















