package com.vo.legendscombat.web.controller;

import com.vo.legendscombat.service.UserService;
import com.vo.legendscombat.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult result, Model model) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match");
        }
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.registerNewUser(request);
        } catch (IllegalArgumentException ex) {
            result.rejectValue("email", "email.exists", ex.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }

    @GetMapping("/")
    public String home(Model model, Authentication auth) {
        model.addAttribute("userEmail", auth.getName());
        return "index";
    }
}
