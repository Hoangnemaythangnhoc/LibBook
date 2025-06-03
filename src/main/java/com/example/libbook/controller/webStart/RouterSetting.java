package com.example.libbook.controller.webStart;

import com.example.libbook.entity.Product;
import com.example.libbook.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping
public class RouterSetting {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = productService.getAllProduct();
        System.out.println("Products for home: " + (products != null ? products.size() : "null"));
        model.addAttribute("products", products);
        return "Mainpage/home";
    }

    @GetMapping("/login")
    public String login(Model model) {
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
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("user") != null) {
            model.addAttribute("user", session.getAttribute("user"));
            System.out.println(session.getAttribute("user"));
        }
        List<Product> products = productService.getAllProduct();
        System.out.println("Products for dashboard: " + (products != null ? products.size() : "null"));
        model.addAttribute("products", products);
        return "Mainpage/home";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", productService.getAllProduct().stream().limit(4).toList());
            return "Mainpage/product";
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/category/{categoryName}")
    public String categoryPage(@PathVariable String categoryName, Model model) {
        List<Product> products = productService.getProductsByCategory(categoryName);
        model.addAttribute("products", products);
        model.addAttribute("categoryName", categoryName);
        return "Mainpage/category";
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        // Redirect to login if not authenticated
        if (session.getAttribute("USER") == null) {
            return "redirect:/login";
        }
        return "CartAndPay/cart";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        if (session.getAttribute("USER") == null) {
            return "Login/login";
        }
        return "profile/profile";
    }

    @GetMapping("/admin")
    public String admin(Model model, HttpSession session) {
        return "profile/admin";
    }

    @GetMapping("/upload-product")
    public String uploadProduct(Model model) {
        List<Product> products = productService.getAllProduct();
        System.out.println("Products for upload page: " + (products != null ? products.size() : "null"));
        model.addAttribute("products", products);
        return "Mainpage/upload-product";
    }

    @GetMapping("/staff")
    public String staffPanel(Model model, HttpSession session) {
        // Giả định lấy thông tin nhân viên từ session
        if (session.getAttribute("staff") != null) {
            model.addAttribute("staffName", session.getAttribute("staff"));
            model.addAttribute("staffEmail", "staff@example.com");
            model.addAttribute("staffPhone", "0123456789");
        }
        return "profile/staff";
    }
}