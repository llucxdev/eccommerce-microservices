package org.example.purchaseapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PurchaseRequest {
    @NotNull(message = "productId cannot be null")
    private Long productId;
    @NotNull(message = "quantity cannot be null")
    @Positive(message = "quantity must be a positive number")
    private int quantity;

    public PurchaseRequest() {
    }

    public PurchaseRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
