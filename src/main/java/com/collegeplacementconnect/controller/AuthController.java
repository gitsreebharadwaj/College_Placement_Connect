package com.collegeplacementconnect.controller;

import com.collegeplacementconnect.dto.UserRegistrationDto;
import com.collegeplacementconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto, 
                                      BindingResult result, 
                                      Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.registerStudent(registrationDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Email already registered!");
            return "register";
        }
        return "redirect:/register?success";
    }
}
