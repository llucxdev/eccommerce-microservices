package org.example.proyectointership.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductCreateDTO {
    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be blank")
    private String name;
    @NotNull(message = "price cannot be null")
    @Positive(message = "price must be a positive number")
    private double price;
    @NotNull(message = "stock cannot be null")
    @Positive(message = "stock must be a positive number")
    private int stock;
    @NotNull(message = "productTypeId cannot be null")
    @Positive(message = "productTypeId must be a positive number")
    private int productTypeId;

    public ProductCreateDTO() {}

    public ProductCreateDTO(String name, double price, int stock, int productTypeId) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.productTypeId = productTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(int productTypeId) {
        this.productTypeId = productTypeId;
    }
}
