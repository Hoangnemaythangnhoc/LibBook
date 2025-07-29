package com.example.libbook.controller.admin;

import com.example.libbook.dto.DashboardData;
import com.example.libbook.dto.MultiChartData;
import com.example.libbook.entity.Product;
import com.example.libbook.service.ProductService;
import jakarta.validation.constraints.Pattern;
import org.springframework.ui.Model;
import com.example.libbook.dto.UserDTO;
import com.example.libbook.service.DashboardService;
import com.example.libbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final DashboardService dashboardService;

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    public AdminUserController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/customers")
    public ResponseEntity<List<UserDTO>> getCustomers() {
        return ResponseEntity.ok(userService.getCustomers());
    }

    @GetMapping("/staff")
    public ResponseEntity<List<UserDTO>> getStaff() {
        return ResponseEntity.ok(userService.getStaffWithRoleName());
    }

    @PostMapping("/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable int id) {
        return userService.banUser(id) ?
                ResponseEntity.ok("User banned") :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to ban");
    }

    @PostMapping("/{id}/unban")
    public ResponseEntity<?> unbanUser(@PathVariable int id) {
        return userService.unbanUser(id) ?
                ResponseEntity.ok("User unbanned") :
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unban");
    }

    @PostMapping("/create-staff")
    public ResponseEntity<?> createStaff(@RequestBody UserDTO dto) {
        try {
            if (dto.getRoleID() < 3 || dto.getRoleID() > 5) {
                return ResponseEntity.badRequest().body("Invalid role");
            }
            boolean success = userService.createStaffAccount(dto);
            return success ?
                    ResponseEntity.ok("Staff created") :
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/dashboard-data")
    public ResponseEntity<DashboardData> getDashboardData(
            @RequestParam(value = "timeType", defaultValue = "1month")
            @Pattern(regexp = "1day|1week|1month|3months|7months|lastyear", message = "Invalid timeType") String timeType) {
        try {
            DashboardData data = dashboardService.getDashboardData(timeType);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/multi-chart-data")
    public ResponseEntity<MultiChartData> getMultiChartData(
            @RequestParam(value = "timeType", defaultValue = "1month")
            @Pattern(regexp = "1day|1week|1month|3months|7months|lastyear", message = "Invalid timeType") String timeType) {
        try {
            MultiChartData data = dashboardService.getMultiChartData(timeType);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateStaffRole(@PathVariable int id, @RequestParam int roleId) {
        try {
            if (roleId < 3 || roleId > 5) {
                return ResponseEntity.badRequest().body("Invalid role");
            }
            boolean success = userService.updateStaffRole(id, roleId);
            return success ?
                    ResponseEntity.ok("Role updated successfully") :
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update role");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("data")
    public ResponseEntity<String> importDataFromExcel(@RequestBody List<Map<String, Object>> products) {
        if (products == null || products.isEmpty()) {
            return ResponseEntity.badRequest().body("No products provided in the request");
        }
        try {
            int[] result = productService.importProducts(products);
            return ResponseEntity.ok(String.format("Imported %d products successfully, %d failed", result[0], result[1]));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing products: " + e.getMessage());
        }
    }
}


