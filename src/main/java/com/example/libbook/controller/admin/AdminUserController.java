package com.example.libbook.controller.admin;

import com.example.libbook.dto.UserDTO;
import com.example.libbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

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
}