package com.example.libbook.controller.rating;

import com.example.libbook.dto.RatingDTO;
import com.example.libbook.dto.UserDTO;
import com.example.libbook.entity.Rating;
import com.example.libbook.entity.User;
import com.example.libbook.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/{productId}")
    public ResponseEntity<List<Rating>> getRatingsByProductId(@PathVariable("productId") int productId) {
        List<Rating> ratings = ratingService.getRatingsByProductId(productId);
        if (ratings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(ratings);
    }

    @PostMapping("/submitted")
    public ResponseEntity<String> submitRating(
            @RequestBody RatingDTO ratingDTO,
            HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("USER");
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in to submit a rating.");
            }

            // Gán userId từ session vào RatingDTO
            ratingDTO.setUserId(currentUser.getUserId());

            boolean result = ratingService.saveRating(ratingDTO);
            if (result) {
                return ResponseEntity.ok("Rating submitted successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit rating.");
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please buy the product before rating.");
        }
    }
}