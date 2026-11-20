package org.example.proyectointership.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public class ProductUpdateDTO {
    private String name;
    @Positive(message = "Price must be a positive number")
    private Double price;

    @Min(value = 0, message = "Stock must be at least 0")
    private Integer stock;

    @Positive(message = "Product type ID must be a positive number")
    private Integer productTypeId;

    public ProductUpdateDTO() {
    }

    public ProductUpdateDTO(String name, Double price, Integer stock, Integer productTypeId) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(Integer productTypeId) {
        this.productTypeId = productTypeId;
    }
}
