package org.ilia.inventoryingapp.http.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public String returnPage(ResponseStatusException exception, Model model) {
        log.warn("Exception in exception handler", exception);
        model.addAttribute("error", exception.getStatusCode().value());
        return "error/error";
    }
}
