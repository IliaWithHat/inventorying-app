package org.ilia.inventoryingapp.http.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String returnPage(Exception exception, Model model) {
        log.error("Exception in exception handler", exception);
        if (exception instanceof ResponseStatusException responseStatusException) {
            model.addAttribute("error", responseStatusException.getStatusCode().value());
        } else {
            model.addAttribute("error", "What are you doing?");
        }
        return "error/error";
    }
}
