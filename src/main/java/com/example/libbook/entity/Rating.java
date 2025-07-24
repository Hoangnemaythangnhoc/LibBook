package com.example.libbook.entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Rating {
    private int ratingId;
    private int productId;
    private int userId;
    private String userName;
    private int stars;
    private LocalDateTime createdAt;
    public String content;
    public boolean Status;
}