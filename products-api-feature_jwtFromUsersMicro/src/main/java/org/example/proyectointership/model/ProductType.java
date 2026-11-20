package org.example.proyectointership.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "product_types")
public class ProductType implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @OneToMany(mappedBy = "productType")
    private List<Product> products;

    public ProductType() {}

    public ProductType(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }

    public ProductType(int id, String name) {
        this.id = id;
        this.name = name;
        this.products = new ArrayList<>();
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

    public String getProducts() {
        return products.toString();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductType that = (ProductType) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(products, that.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, products);
    }
}
