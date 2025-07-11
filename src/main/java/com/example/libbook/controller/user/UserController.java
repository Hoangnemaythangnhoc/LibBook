package com.example.libbook.controller.user;


import com.example.libbook.dto.ChangePasswordDTO;
import com.example.libbook.dto.FileUploadDTO;
import com.example.libbook.dto.RatingDTO;
import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.User;
import com.example.libbook.service.EmailTokenService;
import com.example.libbook.service.RatingService;
import com.example.libbook.service.UserService;
import com.example.libbook.utils.ImageUtils;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    private final UserService userService;
    private final EmailTokenService emailTokenService;
    private final ImageUtils imageUtils;

    @Autowired
    public UserController(UserService userService, EmailTokenService emailTokenService, ImageUtils imageUtils) {
        this.userService = userService;
        this.emailTokenService = emailTokenService;
        this.imageUtils = imageUtils;
    }

    @Autowired
    private RatingService ratingService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "/Login/signup";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam("email") String email,
            @RequestParam("password") String pass,
            @RequestParam("newpass") String newpass,
            Model model) {
        try {
            System.out.println(email + pass + " : " + newpass);
            if (!pass.equals(newpass)) {
                model.addAttribute("message", "Password not match!");
                model.addAttribute("messageType", "error");
                return "Login/signup";
            }
            UserDTO userDTO = new UserDTO(email, pass);

            boolean result = userService.createAccount(userDTO);
            if (result) {
                model.addAttribute("message", "Registration successful! Pls Login");
                model.addAttribute("messageType", "success");
                return "Login/login";
            } else {
                model.addAttribute("message", "Registration failed!");
                model.addAttribute("messageType", "error");
                return "Login/signup";
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            model.addAttribute("message", e.getMessage());
            model.addAttribute("messageType", "error");
            return "Login/signup";
        } catch (Exception e) {
            System.out.println(e.getMessage());

            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("messageType", "error");
            return "Login/signup";
        }
    }

    @PostMapping("/login")
    public String Login(@RequestParam("email") String email,
                        @RequestParam("password") String pass,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        User user = userService.checkLogin(email, pass);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Username hoặc Password sai!");
            return "redirect:/login";
        }

        session.setAttribute("USER", user);
        return "redirect:/home";
    }


    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordDTO changePasswordDTO, Model model , RedirectAttributes redirectAttributes) {

        User u = userService.checkLogin(changePasswordDTO.getEmail(), changePasswordDTO.getCurrentPassword());
        if (u == null) {
            redirectAttributes.addFlashAttribute("error", "Old Password sai!");
            return "redirect:/profile/"+changePasswordDTO.getUserId();
        }
        userService.updatePassword(changePasswordDTO.getEmail(), changePasswordDTO.getNewPassword());
        redirectAttributes.addFlashAttribute("error", "Change password successful!");

        return "redirect:/profile/"+changePasswordDTO.getUserId();
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("error", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile.");
        }
        return "redirect:/profile/" + user.getUserId();

    }

    @PostMapping("profile/{UserID}/avatar/")
    public ResponseEntity<String> uploadAvatar(@PathVariable("UserID") int UserID, @RequestBody FileUploadDTO base64) throws IOException {
        byte[] image = imageUtils.decodeBase64(base64.getImageFile());
        boolean check = userService.uploadAvatar(image, UserID);
        if (check) {
            return ResponseEntity.ok("Avatar updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update avatar");
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable("id") int id) {
        User user = userService.getUserByUserId(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> sendToken(@RequestParam String email, RedirectAttributes redirectAttributes) {
        Map<String, Object> response = new HashMap<>();
        if (email == null || email.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email is required.");
            response.put("redirect", "/Login/forgot-password");
            return ResponseEntity.badRequest().body(response);
        }
        if (userService.isEmailExist(email)) {
            try {
                String token = emailTokenService.generateAndSendToken(email);
                response.put("status", "success");
                response.put("message", "Token sent to " + email);
                response.put("redirect", "/verify-token?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8));
                redirectAttributes.addFlashAttribute("message", "Token sent to " + email);
                redirectAttributes.addFlashAttribute("email", email);
                return ResponseEntity.ok(response);
            } catch (MessagingException | jakarta.mail.MessagingException e) {
                response.put("status", "error");
                response.put("message", "Failed to send token: " + e.getMessage());
                response.put("redirect", "/forgot-password");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
        response.put("status", "error");
        response.put("message", "The email was not found, please register!");
        response.put("redirect", "/Login/signup");
        return ResponseEntity.badRequest().body(response);
    }



    @PostMapping("/verify-token")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestParam String token, @RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        if (email == null || email.isEmpty() || token == null || token.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email and token are required.");
            response.put("redirect", "/verify-token");
            response.put("email", email);
            return ResponseEntity.badRequest().body(response);
        }
        try {
            if (emailTokenService.isValidToken(token, email)) {
                response.put("status", "success");
                response.put("message", "Token verified successfully!");
                response.put("redirect", "/reset-password");
                response.put("email", email);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Invalid or expired token.");
                response.put("redirect", "/verify-token");
                response.put("email", email);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error verifying token: " + e.getMessage());
            response.put("redirect", "/verify-token");
            response.put("email", email);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestParam String email,
                                                             @RequestParam String password,
                                                             @RequestParam("confirm-password") String confirmPassword) {
        Map<String, Object> response = new HashMap<>();

        // Xử lý logic reset password
        if (!password.equals(confirmPassword)) {
            response.put("status", "error");
            response.put("message", "Passwords do not match");
            return ResponseEntity.badRequest().body(response);
        }

        // Thực hiện reset password
        try {
            userService.updatePassword(email,password);
            response.put("status", "success");
            response.put("redirect", "/login");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to reset password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/send-message")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        response.put("reply", "Thank you for your message: " + request.get("message"));
        return ResponseEntity.ok(response);
    }


    @PostMapping("/ratings/submit")
    public ResponseEntity<String> submitRating(
            @RequestBody RatingDTO ratingDTO,
            HttpSession session,
            Model model) {
        try {
            UserDTO currentUser = (UserDTO) session.getAttribute("USER");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in to submit a rating.");
            }
            ratingDTO.setUserId(currentUser.getUserId());
            boolean result = ratingService.saveRating(ratingDTO);
            if (result) {
                return ResponseEntity.ok("Rating submitted successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit rating.");
            }
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("messageType", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}