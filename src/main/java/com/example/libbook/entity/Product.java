package com.example.libbook.entity;

public class Product {
    private Long productId;
    private String productName;
    private String description;
    private int buys;
    private int available;
    private double price;
    private String imageFile;
    private Long userId;
    private int status;
    private double rating;

    // Constructors
    public Product() {}

    public Product(Long productId, String productName, String description, int buys, int available, double price, String imageFile, Long userId, int status, double rating) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.buys = buys;
        this.available = available;
        this.price = price;
        this.imageFile = imageFile;
        this.userId = userId;
        this.status = status;
        this.rating = rating;
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getBuys() { return buys; }
    public void setBuys(int buys) { this.buys = buys; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageFile() { return imageFile; }
    public void setImageFile(String imageFile) { this.imageFile = imageFile; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}