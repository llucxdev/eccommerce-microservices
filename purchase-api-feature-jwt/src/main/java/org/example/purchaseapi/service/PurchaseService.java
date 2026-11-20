package org.example.purchaseapi.service;

import org.example.purchaseapi.client.ProductClient;
import org.example.purchaseapi.client.UserClient;
import org.example.purchaseapi.dto.ProductResponse;
import org.example.purchaseapi.dto.PurchaseRequest;
import org.example.purchaseapi.dto.PurchaseResponse;
import org.example.purchaseapi.exception.NotEnoughStockException;
import org.example.purchaseapi.exception.PurchaseNotFoundException;
import org.example.purchaseapi.exception.UserNotFoundException;
import org.example.purchaseapi.model.Purchase;
import org.example.purchaseapi.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProductClient productClient;

    public List<PurchaseResponse> getAllPurchases() {
        List<Purchase> purchases = purchaseRepository.findAll();
        List<PurchaseResponse> purchaseResponses = new ArrayList<>();
        for (Purchase purchase : purchases) {
            purchaseResponses.add(convertToResponse(purchase));
        }
        return purchaseResponses;
    }

    public List<PurchaseResponse> getAllPurchasesByUser(String token) {
        Long userId = Long.parseLong(userClient.getClaims(token).get("userId"));
        List<Purchase> purchases = purchaseRepository.findAllByUserId(userId);
        List<PurchaseResponse> purchaseResponses = new ArrayList<>();
        for (Purchase purchase : purchases) {
            purchaseResponses.add(convertToResponse(purchase));
        }
        return purchaseResponses;
    }

    public List<PurchaseResponse> getAllPurchasesByUserIdAndProductType(String token, int productType) {
        Long userId = Long.parseLong(userClient.getClaims(token).get("userId"));
        List<Purchase> purchases = purchaseRepository.findAllByUserId(userId);
        List<PurchaseResponse> purchaseResponses = new ArrayList<>();
        for (Purchase purchase : purchases) {
            ProductResponse productResponse = productClient.getProduct(purchase.getProductId(), token);
            if (productResponse.getProductType().getId() == productType) purchaseResponses.add(convertToResponse(purchase));
        }
        return purchaseResponses;
    }

    public PurchaseResponse getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new PurchaseNotFoundException("Purchase not found"));
        return convertToResponse(purchase);
    }

    public PurchaseResponse createPurchase(PurchaseRequest purchaseRequest, String token) {
        Map<String, String> claims = userClient.getClaims(token);
        ProductResponse product = productClient.getProduct(purchaseRequest.getProductId(), token);
        int stock = product.getStock();
        int quantity = purchaseRequest.getQuantity();
        if (stock >= quantity) {
            productClient.updateProduct(product.getId(), stock - quantity, userClient.getTechnicalToken());
            return convertToResponse(purchaseRepository.save(convertToEntity(Long.parseLong(claims.get("userId")), purchaseRequest)));
        } else {
         throw new NotEnoughStockException("Not enough stock");
        }
    }

    private PurchaseResponse convertToResponse(Purchase purchase) {
        return new PurchaseResponse(purchase.getId(), purchase.getUserId(), purchase.getProductId(), purchase.getQuantity());
    }

    private Purchase convertToEntity(Long userId, PurchaseRequest purchaseRequest) {
        return new Purchase(userId, purchaseRequest.getProductId(), purchaseRequest.getQuantity());
    }
}
