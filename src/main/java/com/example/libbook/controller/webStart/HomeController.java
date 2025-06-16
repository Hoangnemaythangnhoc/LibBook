package com.example.libbook.controller.webStart;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        return "Login/login";
    }


    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        return "Login/forgot-password";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        return "Login/signup";
    }

    @GetMapping("/home")
    public String dashboard(Model model) {
        return "Mainpage/home";
    }

    @GetMapping("/product")
    public String productDetail(Model model) {
        return "Mainpage/product";
    }
}
