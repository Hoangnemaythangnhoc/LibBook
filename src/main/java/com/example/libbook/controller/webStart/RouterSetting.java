package com.example.libbook.controller.webStart;

import com.example.libbook.entity.*;
import com.example.libbook.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



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
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model, HttpSession session ,HttpServletRequest request) {
        if (session.getAttribute("USER") != null) {
            User user = (User) session.getAttribute("USER");
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

        // Auto login via cookie (if not already logged in)
        if (session.getAttribute("USER") == null) {
            Cookie[] cookies = request.getCookies();
            String email = null;
            String password = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("email".equals(cookie.getName())) {
                        email = cookie.getValue();
                    }
                    if ("password".equals(cookie.getName())) {
                        password = cookie.getValue();
                    }
                }
            }

            if (email != null && password != null) {
                User user = userService.checkLogin(email, password);
                if (user != null) {
                    session.setAttribute("USER", user);
                    return "redirect:/home";
                }
            }
        }
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
    public String dashboard(Model model ,  HttpServletRequest request, HttpSession session) {
        if (session.getAttribute("USER") != null) {
            User user = (User) session.getAttribute("USER");
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

        if (session.getAttribute("USER") == null) {
            Cookie[] cookies = request.getCookies();
            String email = null;
            String password = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("email".equals(cookie.getName())) {
                        email = cookie.getValue();
                    }
                    if ("password".equals(cookie.getName())) {
                        password = cookie.getValue();
                    }
                }
            }

            if (email != null && password != null) {
                User user = userService.checkLogin(email, password);
                if (user != null) {
                    session.setAttribute("USER", user);
                    return "redirect:/home";
                }
            }
        }
        model.addAttribute("products", products);
        model.addAttribute("tags", tags);
        return "Mainpage/home";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model, HttpSession session) {
        Product product = productService.getProductById(id);
        List<Tag> tags = tagService.getAllTags();
        if (product != null) {
            System.out.println("Tags for product detail: " + (tags != null ? tags : "No tags fetched"));
            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", productService.getAllProduct().stream().limit(4).toList());
            model.addAttribute("tags", tags);
            User user = (User) session.getAttribute("USER");
            model.addAttribute("USER", user);
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
        model.addAttribute("tagName", "Kết quả tìm kiếm");
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

    @GetMapping("/profile/{id}")
    public String profile(@PathVariable int id,  Model model, HttpSession session) {
        if (session.getAttribute("USER") == null) {
            return "redirect:/login";
        }
        User user = userService.getUserByUserId(id);
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        model.addAttribute("userProfile", user);
        return "profile/profile";
    }

    @GetMapping("/admin")
    public String admin(Model model, HttpSession session) {
        if (session.getAttribute("USER") == null) {
            return "redirect:/login";
        }
        List<Tag> tags = tagService.getAllTags();
        System.out.println("Tags for admin: " + (tags != null ? tags : "No tags fetched"));
        User user = (User) session.getAttribute("USER");
        if (user.getRoleId() != 1) return "redirect:/home";
        model.addAttribute("USER", user);
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
        if (session.getAttribute("USER") == null) {
            return "redirect:/login";
        }
        if (session.getAttribute("staff") != null) {
            model.addAttribute("staffName", session.getAttribute("staff"));
            model.addAttribute("staffEmail", "staff@example.com");
            model.addAttribute("staffPhone", "0123456789");
        }
        List<Product> products = productService.getAllProduct();
        List<Tag> tags = tagService.getAllTags();
        User user = (User) session.getAttribute("USER");
        if (user.getRoleId() != 3) return "redirect:/home";
        model.addAttribute("USER", user);
        model.addAttribute("products", products);
        model.addAttribute("tags", tags);
        return "profile/staff";
    }

    @GetMapping("/customer-care")
    public String customerCarePanel(Model model, HttpSession session) {
        if (session.getAttribute("USER") == null) {
            return "redirect:/login";
        }
        if (session.getAttribute("customer-care") != null) {
            model.addAttribute("customerCareName", session.getAttribute("customer-care"));
            model.addAttribute("customerCareEmail", "customercare@example.com");
            model.addAttribute("customerCarePhone", "0123456789");
        }
        List<Product> products = productService.getAllProduct();
        List<Tag> tags = tagService.getAllTags();
        User user = (User) session.getAttribute("USER");
        if (user.getRoleId() != 4) return "redirect:/home";
        model.addAttribute("products", products);
        model.addAttribute("tags", tags);
        return "profile/customer-care";
    }

    @GetMapping("/shipper")
    public String shipperPanel(Model model, HttpSession session) {
        if (session.getAttribute("USER") == null) {
            return "redirect:/login";
        }
        if (session.getAttribute("shipper") != null) {
            model.addAttribute("shipperName", session.getAttribute("shipper"));
            model.addAttribute("shipperEmail", "shipper@example.com");
            model.addAttribute("shipperPhone", "0901234567");
        }
        List<Product> products = productService.getAllProduct();
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("products", products);
        model.addAttribute("tags", tags);
        User user = (User) session.getAttribute("USER");
        if (user.getRoleId() != 5) return "redirect:/home";
        model.addAttribute("USER", user);
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


    @PostMapping("/custom-logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        Cookie emailCookie = new Cookie("email", null);
        emailCookie.setPath("/");
        emailCookie.setMaxAge(0);
        response.addCookie(emailCookie);
        Cookie passwordCookie = new Cookie("password", null);
        passwordCookie.setPath("/");
        passwordCookie.setMaxAge(0);
        response.addCookie(passwordCookie);

        return "redirect:/login";
    }

    @GetMapping("/chat-widget")
    public String chat(HttpSession session, HttpServletResponse response,Model model,HttpServletRequest request) {
        if (session.getAttribute("USER") != null) {
            User user = (User) session.getAttribute("USER");
            model.addAttribute("USER", user);
        } else {
            System.out.println("User in session is null");
        }


        if (session.getAttribute("USER") == null) {
            Cookie[] cookies = request.getCookies();
            String email = null;
            String password = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("email".equals(cookie.getName())) {
                        email = cookie.getValue();
                    }
                    if ("password".equals(cookie.getName())) {
                        password = cookie.getValue();
                    }
                }
            }

            if (email != null && password != null) {
                User user = userService.checkLogin(email, password);
                if (user != null) {
                    session.setAttribute("USER", user);
                    return "redirect:/home";
                }
            }
        }
        return "fragments/chat-widget";
    }


}