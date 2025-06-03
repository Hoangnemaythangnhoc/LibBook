package com.example.libbook.controller.Product;

import com.example.libbook.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart/api")
public class CartController {

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestBody Map<String, Object> payload,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("CART");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("CART", cart);
            }

            cart.add(payload);
            
            response.put("success", true);
            response.put("message", "Đã thêm vào giỏ hàng");
            response.put("cartSize", cart.size());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCart(
            @RequestBody Map<String, Object> payload,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("CART");
            if (cart == null) {
                throw new RuntimeException("Giỏ hàng không tồn tại");
            }

            String bookId = (String) payload.get("bookId");
            Integer quantity = (Integer) payload.get("quantity");

            boolean found = false;
            for (Map<String, Object> item : cart) {
                if (item.get("bookId").equals(bookId)) {
                    item.put("quantity", quantity);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng");
            }

            response.put("success", true);
            response.put("message", "Đã cập nhật giỏ hàng");
            response.put("cart", cart);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{bookId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @PathVariable String bookId,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("CART");
            if (cart == null) {
                throw new RuntimeException("Giỏ hàng không tồn tại");
            }

            cart.removeIf(item -> item.get("bookId").equals(bookId));

            response.put("success", true);
            response.put("message", "Đã xóa sản phẩm khỏi giỏ hàng");
            response.put("cartSize", cart.size());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            session.removeAttribute("CART");
            response.put("success", true);
            response.put("message", "Đã xóa giỏ hàng");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkout(
            @RequestBody Map<String, Object> orderData,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserDTO user = (UserDTO) session.getAttribute("USER");
            if (user == null) {
                throw new RuntimeException("Vui lòng đăng nhập để tiếp tục");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("CART");
            if (cart == null || cart.isEmpty()) {
                throw new RuntimeException("Giỏ hàng trống");
            }

            // Process order (mocked for now)
            String orderId = "ORD" + System.currentTimeMillis();

            session.removeAttribute("CART");

            response.put("success", true);
            response.put("orderId", orderId);
            response.put("message", "Đặt hàng thành công!");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/items")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartItems(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cart = (List<Map<String, Object>>) session.getAttribute("CART");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("CART", cart);
            }

            response.put("success", true);
            response.put("items", cart);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}