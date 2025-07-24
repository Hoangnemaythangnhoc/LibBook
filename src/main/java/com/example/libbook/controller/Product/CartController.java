package com.example.libbook.controller.Product;

import ch.qos.logback.core.model.Model;
import com.example.libbook.dto.CartItemDTO;
import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.CartItem;
import com.example.libbook.entity.User;
import com.example.libbook.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private CartService cartService;

    @GetMapping("/{id}")
    public ResponseEntity<Integer> cart(@PathVariable int id) {
        int count = cartService.getCartItemCount(id);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItemToCart(@RequestBody CartItem cartItem, HttpSession session) {
        User user = (User) session.getAttribute("USER");
        if (user == null) {
            return new ResponseEntity<>("login", HttpStatus.UNAUTHORIZED);
        }
        cartService.addOrUpdateCartItem(user.getUserId(), cartItem.getProductId(), cartItem.getQuantity());
        return new ResponseEntity<>("success", HttpStatus.OK);
    }


    @PostMapping("/delete")
    public ResponseEntity<?> deleteItemFromCart(@RequestBody CartItem cartItem, HttpSession session) {
        User user = (User) session.getAttribute("USER");
        if (user == null) {
            return new ResponseEntity<>("login", HttpStatus.UNAUTHORIZED);
        }
        cartService.addOrUpdateCartItem(user.getUserId(), cartItem.getProductId(), cartItem.getQuantity());
        return new ResponseEntity<>("success", HttpStatus.OK);
    }



    @PostMapping("delete/allItems")
    public ResponseEntity<?> deleteAllItemFromCart( HttpSession session) {
        User user = (User) session.getAttribute("USER");
        if (user == null) {
            return new ResponseEntity<>("login", HttpStatus.UNAUTHORIZED);
        }
        cartService.deleteAllCartItem(user.getUserId());
        return new ResponseEntity<>("success", HttpStatus.OK);
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
    public ResponseEntity<List<CartItemDTO>> getCartItems(HttpSession session) {
        User _u = (User) session.getAttribute("USER");
        List<CartItemDTO> cartItems = cartService.getCartItemsByUserId(_u.getUserId());
        if (cartItems == null || cartItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(cartItems);
    }
}