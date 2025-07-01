package com.example.libbook.service;

import com.example.libbook.dto.RatingDTO;
import com.example.libbook.entity.Rating;

import java.util.List;

public interface RatingService {
    List<Rating> getRatingsByProductId(int productId);
    boolean saveRating(RatingDTO ratingDTO);
}
