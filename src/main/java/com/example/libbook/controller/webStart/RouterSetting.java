package com.example.libbook.controller.webStart;

import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.Order;
import com.example.libbook.entity.OrderStatus;
import com.example.libbook.entity.Product;
import com.example.libbook.entity.Tag;
import com.example.libbook.service.OrderService;
import com.example.libbook.service.OrderStatusService;
import com.example.libbook.service.ProductService;
import com.example.libbook.service.TagService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class RouterSetting {

    @Autowired
    private ProductService productService;

    @Autowired
    private TagService tagService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderStatusService orderStatusService;

    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = productService.getAllProduct().stream()
                .map(product -> {
                    if (product.getImageFile() == null) {
                        product.setImageFile("https://static.vecteezy.com/system/resources/previews/017/222/245/non_2x/3d-stack-of-books-3d-rendering-illustration-free-png.png");
                    } else if (!(product.getImageFile() instanceof String)) {
                        product.setImageFile("default.jpg");
                    }
                    return product;
                })
                .collect(Collectors.toList());
        List<Tag> tags = tagService.getAllTags();
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
        if (session.getAttribute("USER") != null) {
            UserDTO user = (UserDTO) session.getAttribute("USER");
            model.addAttribute("USER", user);
            System.out.println("User in session - UserId: " + user.getUserId() +
                    ", UserName: " + user.getUserName() +
                    ", Email: " + user.getEmail());
        } else {
            System.out.println("User in session is null");
        }
        List<Product> products = productService.getAllProduct();
        List<Tag> tags = tagService.getAllTags();
        System.out.println("User in session: " + session.getAttribute("USER"));
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

    @GetMapping("/verify-token")
    public String showVerifyTokenPage() {
        return "Login/verify-token"; // Trả về template Thymeleaf verify-token.html
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "Login/reset-password";
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

    @GetMapping("/shipper")
    public String shipperPanel(Model model, HttpSession session) {
        if (session.getAttribute("shipper") != null) {
            model.addAttribute("shipperName", session.getAttribute("shipper"));
            model.addAttribute("shipperEmail", "shipper@example.com");
            model.addAttribute("shipperPhone", "0901234567");
        }
        List<Product> products = productService.getAllProduct();
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("products", products);
        model.addAttribute("tags", tags);
        return "profile/shipper";
    }

    @GetMapping("/admin/edit-book/{id}")
    public String editBookForAdmin(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        List<Tag> tags = tagService.getAllTags();
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("tags", tags);
            model.addAttribute("isAdmin", true);
            return "Mainpage/edit-book";
        } else {
            return "redirect:/admin";
        }
    }

    @GetMapping("/staff/edit-book/{id}")
    public String editBookForStaff(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        List<Tag> tags = tagService.getAllTags();
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("tags", tags);
            model.addAttribute("isAdmin", false);
            return "Mainpage/edit-book";
        } else {
            return "redirect:/staff";
        }
    }
}