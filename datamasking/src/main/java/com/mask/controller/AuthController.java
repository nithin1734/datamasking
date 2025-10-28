package com.mask.controller;

import com.mask.dto.UserRegistrationDto;
import com.mask.model.User;
import com.mask.service.EmailService;
import com.mask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully!");
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "signup";
        }

        try {
            User user = userService.registerUser(registrationDto);

            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getName());
            } catch (Exception e) {
                System.err.println("Failed to send welcome email: " + e.getMessage());
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please login with your credentials.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/signup";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No account found with this email address.");
                return "redirect:/forgot-password";
            }

            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            emailService.sendPasswordResetEmail(user.getEmail(), token);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Password reset link has been sent to your email address.");
            return "redirect:/forgot-password";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An error occurred while processing your request. Please try again.");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam("token") String token, Model model) {

        if (!userService.validatePasswordResetToken(token)) {
            model.addAttribute("errorMessage", "Invalid or expired reset token.");
            return "login";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {

        if (!userService.validatePasswordResetToken(token)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid or expired reset token.");
            return "redirect:/login";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/reset-password?token=" + token;
        }

        if (!userService.isValidPassword(password)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Password must contain at least 8 characters including uppercase, lowercase, number and special character.");
            return "redirect:/reset-password?token=" + token;
        }

        try {
            User user = userService.getUserByPasswordResetToken(token);
            userService.changeUserPassword(user, password);
            userService.invalidatePasswordResetToken(token);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Password has been reset successfully. Please login with your new password.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An error occurred while resetting your password. Please try again.");
            return "redirect:/reset-password?token=" + token;
        }
    }
}
