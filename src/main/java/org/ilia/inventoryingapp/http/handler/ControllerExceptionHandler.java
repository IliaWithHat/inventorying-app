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
        if (exception instanceof ResponseStatusException responseStatusException) {
            log.warn("Exception in exception handler", exception);
            model.addAttribute("error", responseStatusException.getStatusCode().value());
        } else {
            log.error("Exception in exception handler", exception);
            model.addAttribute("error", "What are you doing?");
        }
        return "error/error";
    }
}
