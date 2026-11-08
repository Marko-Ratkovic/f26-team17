package com.csc340.glowup.mvc;

import com.csc340.glowup.service.BadRequestException;
import com.csc340.glowup.service.ConflictException;
import com.csc340.glowup.service.ForbiddenException;
import com.csc340.glowup.service.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;


// Shows shared/error.ftlh for errors thrown by the page controllers.
@ControllerAdvice(assignableTypes = {AppController.class, CustomerUiController.class, ProviderUiController.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UiExceptionHandler {
    
    // makes userRole and userName available to the nav on every page of these controllers
    @ModelAttribute
    public void addSessionUser(HttpSession session, Model model) {
        copySessionUser(session, model);
    }

    @ExceptionHandler({BadRequestException.class, ConflictException.class, NotFoundException.class, ForbiddenException.class})
    public String handleServiceError(RuntimeException exception, HttpSession session,
                                     Model model, HttpServletResponse response) {
        copySessionUser(session, model);
        model.addAttribute("errorMessage", exception.getMessage());
        if (exception instanceof NotFoundException) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else if (exception instanceof ConflictException) {
            response.setStatus(HttpStatus.CONFLICT.value());
        } else if (exception instanceof ForbiddenException) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return "shared/error";
    }

    @ExceptionHandler(BindException.class)
    public String handleValidationError(BindException exception, HttpSession session,
                                        Model model, HttpServletResponse response) {
        copySessionUser(session, model);
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Please check the information you entered and try again.");
        model.addAttribute("errorMessage", message);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return "shared/error";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataConflict(HttpSession session, Model model, HttpServletResponse response) {
        copySessionUser(session, model);
        model.addAttribute("errorMessage", "The change conflicts with existing data. Please check the information you entered and try again.");
        response.setStatus(HttpStatus.CONFLICT.value());
        return "shared/error";
    }

    // Helper method to copy user info from session to model
    private void copySessionUser(HttpSession session, Model model) {
        Object role = session.getAttribute("userRole");
        Object name = session.getAttribute("userName");

        if (role != null) {
            model.addAttribute("userRole", role);
        }
        if (name != null) {
            model.addAttribute("userName", name);
        }
    }
}
