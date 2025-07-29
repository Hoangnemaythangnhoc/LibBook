package com.example.libbook.controller.user;

import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.Product;
import com.example.libbook.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products/{id}")
    public String getProductDetails(@PathVariable("id") int productId, Model model, HttpSession session) {
        Product product = productService.getProductById((long) productId);
        model.addAttribute("product", product);
        UserDTO currentUser = (UserDTO) session.getAttribute("USER");
        int userId = currentUser != null ? currentUser.getUserId() : 0;
        model.addAttribute("userId", userId);
        return "product";
    }
}
