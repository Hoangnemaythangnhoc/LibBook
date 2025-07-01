package com.example.libbook.repository;

import com.example.libbook.entity.Rating;

import java.util.List;

public interface RatingRepository {
    List<Rating> getRatingsByProductId(int productId);
    boolean saveRating(Rating rating);
}
