package com.orderManagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="t_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productCode;
    private int quantity;
}
