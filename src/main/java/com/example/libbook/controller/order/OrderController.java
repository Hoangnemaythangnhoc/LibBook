package com.example.libbook.controller.order;

import com.example.libbook.entity.Order;
import com.example.libbook.entity.OrderStatus;
import com.example.libbook.service.OrderService;
import com.example.libbook.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderStatusService orderStatusService;

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        System.out.println("OrderController: Received request to fetch all orders");
        try {
            List<Order> orders = orderService.getAllOrders();
            System.out.println("OrderController: Found " + orders.size() + " orders");
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            System.err.println("OrderController: Error fetching orders: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer orderId) {
        System.out.println("OrderController: Received request to fetch order with id: " + orderId);
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                System.out.println("OrderController: Order with id " + orderId + " not found");
                return ResponseEntity.notFound().build();
            }
            System.out.println("OrderController: Found order with id: " + order.getOrderId());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            System.err.println("OrderController: Error fetching order with id " + orderId + ": " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Integer orderId, @RequestBody Map<String, Integer> request) {
        System.out.println("OrderController: Received request to update status for order id: " + orderId);
        try {
            Integer newStatusId = request.get("orderStatusId");
            if (newStatusId == null) {
                System.out.println("OrderController: Invalid request - orderStatusId is null");
                return ResponseEntity.badRequest().build();
            }
            orderService.updateOrderStatus(orderId, newStatusId);
            System.out.println("OrderController: Updated status for order id: " + orderId + " to status: " + newStatusId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("OrderController: Error updating order status for id " + orderId + ": " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/order-statuses")
    public ResponseEntity<List<OrderStatus>> getAllOrderStatuses() {
        System.out.println("OrderController: Received request to fetch all order statuses");
        try {
            List<OrderStatus> statuses = orderStatusService.getAllOrderStatuses();
            System.out.println("OrderController: Found " + statuses.size() + " order statuses");
            return ResponseEntity.ok(statuses);
        } catch (Exception e) {
            System.err.println("OrderController: Error fetching order statuses: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestBody Map<String, Integer> request) {
        try {
            int orderId = request.get("orderId");
            int userId = request.get("userId");
            Order order = orderService.getOrderById(orderId);
            if (order.getUserId() != userId) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Không có quyền hủy đơn hàng"));
            }
            if (order.getOrderStatusId() > 2) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Đơn hàng không thể hủy"));
            }
            if (orderService.cancelOrder(order)) {
                return ResponseEntity.ok(Map.of("success", true));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Hủy đơn hàng thất bại"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}