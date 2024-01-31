package org.ilia.inventoryingapp.http.controller;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.dto.UserDto;
import org.ilia.inventoryingapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String findAll(@AuthenticationPrincipal UserDetails userDetails,
                          Model model) {
        List<UserDto> users = userService.findAll(userDetails);
        model.addAttribute("users", users);
        return "user/users";
    }

    @GetMapping("/{id}")
    public UserDto findById(@AuthenticationPrincipal UserDetails userDetails,
                            @PathVariable Integer id) {
        return userService.findById(id, userDetails)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Integer id) {
        return userService.update(userDto, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        if (!userService.delete(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}

