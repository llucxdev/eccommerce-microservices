package org.example.purchaseapi.dto;

import java.util.Objects;

public class PurchaseResponse {
    private Long purchaseId;
    private Long userId;
    private Long productId;
    private int quantity;

    public PurchaseResponse() {
    }

    public PurchaseResponse(Long purchaseId, Long userId, Long productId, int quantity) {
        this.purchaseId = purchaseId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseResponse that = (PurchaseResponse) o;
        return quantity == that.quantity && Objects.equals(purchaseId, that.purchaseId) && Objects.equals(userId, that.userId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseId, userId, productId, quantity);
    }
}
