package com.example.libbook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CheckBuy")
@Setter
@Getter
public class CheckBuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CheckBuyId")
    private int checkBuyId;

    @Column(name = "UserId", nullable = false)
    private int userId;

    @Column(name = "ProductId", nullable = false)
    private int productId;

    @Column(name = "Status", nullable = false)
    private int status;
}
