package com.example.libbook.controller.user;

import com.example.libbook.dto.FileUploadDTO;
import com.example.libbook.dto.RatingDTO;
import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.User;
import com.example.libbook.service.EmailTokenService;
import com.example.libbook.service.RatingService;
import com.example.libbook.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailTokenService emailTokenService;
    @Autowired
    private RatingService ratingService; // Thêm khai báo và tiêm dependency này
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
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String pass, HttpSession session, Model model) {
        UserDTO user = userService.checkLogin(email, pass);
        session.setAttribute("USER", user);
        model.addAttribute("user", user);
        return "redirect:/home";
    }

    @PostMapping("/{UserID}/avatar/")
    @ResponseBody
    public ResponseEntity<String> uploadAvatar(@PathVariable("UserID") int UserID, @RequestBody FileUploadDTO base64) throws IOException {
        boolean check = userService.uploadAvatar(base64.getImageFile(), UserID);
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

    @PostMapping("/email")
    public ResponseEntity<String> sendToken(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }

        String token = emailTokenService.generateAndSendToken(email);
        return ResponseEntity.ok("Token sent to " + email + ": " + token);
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
