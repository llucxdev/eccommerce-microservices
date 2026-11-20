package org.example.proyectointership.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private double price;
    private int stock;
    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private ProductType productType;

    public Product() {
    }

    public Product(String name, double price, int stock, ProductType productType) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.productType = productType;
    }

    public Product(int id, String name, double price, int stock, ProductType productType) {
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

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Double.compare(price, product.price) == 0 && stock == product.stock && Objects.equals(name, product.name) && Objects.equals(productType, product.productType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, stock, productType);
    }
}
