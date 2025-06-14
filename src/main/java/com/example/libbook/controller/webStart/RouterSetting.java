package com.example.libbook.controller.webStart;

import com.example.libbook.entity.Product;
import com.example.libbook.entity.Tag;
import com.example.libbook.service.ProductService;
import com.example.libbook.service.TagService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping
public class RouterSetting {

    @Autowired
    private ProductService productService;

    @Autowired
    private TagService tagService;

    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = productService.getAllProduct();
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Products for home: " + (products != null ? products.size() : "0"));
        System.out.println("Tags for home: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("products", products);
        model.addAttribute("tags", tags);
        return "Mainpage/home";
    }

    @GetMapping("/login")
    public String login(Model model) {
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Tags for login: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("tags", tags);
        return "Login/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Tags for forgot-password: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("tags", tags);
        return "Login/forgot-password";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Tags for signup: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("tags", tags);
        return "Login/signup";
    }

    @GetMapping("/home")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("user") != null) {
            model.addAttribute("user", session.getAttribute("user"));
            System.out.println("User in session: " + session.getAttribute("user"));
        }
        List<Product> products = productService.getAllProduct();
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Products for dashboard: " + (products != null ? products.size() : "0"));
        System.out.println("Tags for dashboard: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("products", products);
        model.addAttribute("tags", tags);
        return "Mainpage/home";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        List<Tag> tags = tagService.getAllTags();
        if (product != null) {
            System.out.println("Tags for product detail: " + (tags != null ? tags : "No tags fetched"));
            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", productService.getAllProduct().stream().limit(4).toList());
            model.addAttribute("tags", tags);
            return "Mainpage/product";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/category/{tagName}")
    public String categoryPage(@PathVariable String tagName,
                               @RequestParam(value = "searchQuery", required = false) String searchQuery,
                               Model model) {
        List<Product> products = productService.getProductsByTag(tagName);
        List<Tag> tags = tagService.getAllTags();
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getProductName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchQuery.toLowerCase())))
                    .toList();
        }
        System.out.println("Products for tag '" + tagName + "': " + (products != null ? products.size() : "0"));
        System.out.println("Tags for category page: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("products", products);
        model.addAttribute("tagName", tagName);
        model.addAttribute("tags", tags);
        return "Mainpage/category";
    }

    @GetMapping("/category/search")
    public String searchProducts(@RequestParam(value = "searchQuery", required = false) String searchQuery,
                                 Model model) {
        List<Product> products = productService.getAllProduct();
        List<Tag> tags = tagService.getAllTags();
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getProductName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchQuery.toLowerCase())))
                    .toList();
        }
        System.out.println("Products for search: " + (products != null ? products.size() : "0"));
        System.out.println("Tags for search page: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("products", products);
        model.addAttribute("tagName", "Search Results");
        model.addAttribute("tags", tags);
        return "Mainpage/category";
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpSession session) {
        if (session.getAttribute("USER") == null) {
            return "redirect:/login";
        }
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Tags for cart: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("tags", tags);
        return "CartAndPay/cart";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        if (session.getAttribute("USER") == null) {
            return "redirect:/login";
        }
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Tags for profile: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("tags", tags);
        return "profile/profile";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Tags for admin: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("tags", tags);
        return "profile/admin";
    }

    @GetMapping("/upload-product")
    public String uploadProduct(Model model) {
        List<Product> products = productService.getAllProduct();
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Products for upload page: " + (products != null ? products.size() : "0"));
        System.out.println("Tags for upload-product: " + (tags != null ? tags : "No tags fetched"));
        model.addAttribute("products", products);
        model.addAttribute("tags", tags);
        return "Mainpage/upload-product";
    }

    @GetMapping("/staff")
    public String staffPanel(Model model, HttpSession session) {
        if (session.getAttribute("staff") != null) {
            model.addAttribute("staffName", session.getAttribute("staff"));
            model.addAttribute("staffEmail", "staff@example.com");
            model.addAttribute("staffPhone", "0123456789");
        }
        List<Product> products = productService.getAllProduct();
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("products", products);
        model.addAttribute("tags", tags);
        return "profile/staff";
    }

    @GetMapping("/edit-book/{id}")
    public String editBook(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        List<Tag> tags = tagService.getAllTags();
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("tags", tags);
            return "Mainpage/edit-book";
        } else {
            return "redirect:/staff";
        }
    }

}