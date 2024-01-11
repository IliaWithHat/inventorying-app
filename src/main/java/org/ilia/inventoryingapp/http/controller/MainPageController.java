package org.ilia.inventoryingapp.http.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainPageController {

    @GetMapping("/")
    @ResponseBody
    public String getMainPage() {
        return "<H3>Проходи, не задерживайся.</H3>";
    }
}
