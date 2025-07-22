package com.example.libbook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String productName;
    private String description;
    private double price;
    private String imageFile;
    private int buys;
    private int available;
    private Long userId;
    private int status;
    private double rating;
    private Timestamp createAt;
    private String author;
    private int discount;
    private String publisher;
    // Constructors
    public Product() {}

    public Product(Long productId, String productName, String description,
                   int buys, int available, double price, String imageFile,
                   Long userId, int status, double rating, String author, Timestamp createAt, int discount, String publisher) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.imageFile = imageFile;
        this.userId = userId;
        this.buys = buys;
        this.available = available;
        this.status = status;
        this.rating = rating;
        this.author = author;
        this.createAt = createAt;
        this.discount = discount;
        this.publisher = publisher;
    }

}