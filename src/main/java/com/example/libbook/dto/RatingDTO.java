package com.example.libbook.dto;

public class RatingDTO {
    private int userId;
    private int productId;
    private int stars;
    private String content;

    // Constructors
    public RatingDTO() {}
    public RatingDTO(int userId, int productId, int stars, String content) {
        this.userId = userId;
        this.productId = productId;
        this.stars = stars;
        this.content = content;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}