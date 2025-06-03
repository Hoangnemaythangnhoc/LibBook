package com.example.libbook.controller.user;


import com.example.libbook.dto.UserDTO;
import com.example.libbook.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

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

}
