package org.example.proyectointership.response;

import java.util.Objects;

public class ProductResponse {
    private int id;
    private String name;
    private double price;
    private int stock;
    private ProductTypeResponse productType;

    public ProductResponse() {
    }

    public ProductResponse(int id, String name, double price, int stock, ProductTypeResponse productType) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.productType = productType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ProductTypeResponse getProductType() {
        return productType;
    }

    public void setProductType(ProductTypeResponse productType) {
        this.productType = productType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductResponse that = (ProductResponse) o;
        return id == that.id && Double.compare(price, that.price) == 0 && stock == that.stock && Objects.equals(name, that.name) && Objects.equals(productType, that.productType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, stock, productType);
    }
}
