package org.example.responsiveuserregistration.controller;

import jakarta.validation.Valid;
import org.example.responsiveuserregistration.model.User;
import org.example.responsiveuserregistration.repository.UserRepository;
import org.example.responsiveuserregistration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    //Get Mappings
    @GetMapping("/users")
    public String getUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/account")
    public String account(@AuthenticationPrincipal UserDetails userdetails, Model model) {
        String username = userdetails.getUsername();
        model.addAttribute("username", username);
        return "account";
    }

    //Post Mappings
//    @PostMapping("/login")
//    public String loginUser(@RequestParam String username, @RequestParam String password, Model model) {
//        System.out.println("login request received with username: " + username + " and password: " + password);
//        if (userService.authenticateUser(username, password)) {
//            return "redirect:/users";
//        }
//        else {
//            model.addAttribute("errorMessage", "Invalid username or password");
//            return "login";
//        }
//
//    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }


}
