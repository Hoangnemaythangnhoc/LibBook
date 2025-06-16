package com.example.libbook.controller.user;


import com.example.libbook.dto.FileUploadDTO;
import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.User;
import com.example.libbook.service.EmailTokenService;
import com.example.libbook.service.UserService;
import jakarta.servlet.http.HttpSession;
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
    EmailTokenService emailTokenService;


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "/Login/signup"; // Trả về template signup.html
    }

    @PostMapping("/register")
    public String register(
            @RequestParam("email") String email,
            @RequestParam("password") String pass,
            @RequestParam("newpass") String newpass,
            Model model) {
        try {
            System.out.println(email+pass+" : "+newpass);
            // Kiểm tra email và lưu tài khoản
            if (!pass.equals(newpass)) {
                model.addAttribute("message", "Password not match!");
                model.addAttribute("messageType", "error");
                return "Login/signup";
            }
            UserDTO userDTO = new UserDTO(email,pass);

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
            System.out.println( e.getMessage());
            model.addAttribute("message", e.getMessage());
            model.addAttribute("messageType", "error");
            return "Login/signup";
        } catch (Exception e) {
            System.out.println( e.getMessage());

            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("messageType", "error");
            return "Login/signup";
        }
    }

    @PostMapping("/login")
    public String Login(@RequestParam("email") String email,
                        @RequestParam("password") String pass, HttpSession session, Model model){
        UserDTO user = userService.checkLogin(email,pass);
        session.setAttribute("USER",user);
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
    public ModelAndView getUserById(@PathVariable("id") int ID) {
        User user = userService.getUserByUserId(ID);
        ModelAndView mav = new ModelAndView("userDetail"); // userDetail.jsp hoặc userDetail.html
        mav.addObject("user", user);
        return mav;
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

}