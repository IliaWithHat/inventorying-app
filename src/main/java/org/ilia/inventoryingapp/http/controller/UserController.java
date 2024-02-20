package org.ilia.inventoryingapp.http.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.OptionsForIsOwnedByEmployee;
import org.ilia.inventoryingapp.dto.ItemFilterDto;
import org.ilia.inventoryingapp.dto.UserDto;
import org.ilia.inventoryingapp.service.ItemFilterService;
import org.ilia.inventoryingapp.service.UserService;
import org.ilia.inventoryingapp.validation.groups.CreateUser;
import org.ilia.inventoryingapp.validation.groups.UpdateUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ItemFilterService itemFilterService;

    @GetMapping
    public String findAll(@AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
        List<UserDto> users = userService.findAll(userDetails);
        model.addAttribute("users", users);
        return "user/users";
    }

    @GetMapping("/{id}")
    public String findById(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable Integer id,
                           Model model) {
        UserDto userDto = userService.findById(id, userDetails)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("user", userDto);

        ItemFilterDto itemFilterDto = itemFilterService.findItemFilterByUserId(id);
        model.addAttribute("itemFilter", itemFilterDto);
        model.addAttribute("optionsForIsOwnedByEmployee", OptionsForIsOwnedByEmployee.values());

        if (userDto.getAdminId() == null)
            model.addAttribute("admin", new Object());

        return "user/user";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal UserDetails userDetails,
                         @Validated({Default.class, CreateUser.class}) UserDto userDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
        } else {
            userService.create(userDetails, userDto);
        }
        return "redirect:/admin/users";
    }

    @PutMapping("/{id}")
    public String update(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Integer id,
                         @Validated({Default.class, UpdateUser.class}) UserDto userDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
        } else {
            userService.update(userDto, id, userDetails)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            redirectAttributes.addFlashAttribute("savedUser", "User successfully updated!!!");
        }
        return "redirect:/admin/users/{id}";
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Integer id,
                         HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         @CurrentSecurityContext SecurityContext securityContext) {
        Optional<UserDto> user = userService.delete(id, userDetails);
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        if (user.get().getAdminId() == null) {
            new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, securityContext.getAuthentication());
            return "<script>window.location.replace('/login');</script>";
        }
        return "<script>window.close();</script>";
    }
}

