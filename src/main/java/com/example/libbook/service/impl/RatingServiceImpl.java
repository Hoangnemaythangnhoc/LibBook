package com.example.libbook.service.impl;

import com.example.libbook.dto.RatingDTO;
import com.example.libbook.entity.CheckBuy;
import com.example.libbook.entity.Rating;
import com.example.libbook.entity.User;
import com.example.libbook.repository.CheckBuyRepository;
import com.example.libbook.repository.RatingRepository;
import com.example.libbook.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private CheckBuyRepository checkBuyRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Rating> getRatingsByProductId(int productId) {
        List<Rating> ratings = ratingRepository.getRatingsByProductId(productId);
        for (Rating rating : ratings) {
            try {
                String url = "http://localhost:8080/" + rating.getUserId();
                User user = restTemplate.getForObject(url, User.class);
                if (user != null) {
                    rating.setUserName(user.getUserName());
                } else {
                    rating.setUserName("Unknown");
                }
            } catch (Exception e) {
                rating.setUserName("Error");
            }
        }
        return ratings;
    }

    @Override
    public boolean saveRating(RatingDTO ratingDTO) {
        CheckBuy checkBuy = new CheckBuy();
        checkBuy.setUserId(ratingDTO.getUserId());
        checkBuy.setProductId(ratingDTO.getProductId());
        checkBuy.setStatus(1);

        try {
            boolean checkBuyResult = checkBuyRepository.saveCheckBuy(checkBuy);
            if (!checkBuyResult) {
                throw new IllegalStateException("You have already rated this product.");
            }

            Rating rating = new Rating();
            rating.setUserId(ratingDTO.getUserId());
            rating.setProductId(ratingDTO.getProductId());
            rating.setStars(ratingDTO.getStars());
            rating.setContent(ratingDTO.getContent());
            rating.setCreatedAt(LocalDateTime.now());
            return ratingRepository.saveRating(rating);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("You have not purchased this product, so you cannot rate it.");
        }
    }
}