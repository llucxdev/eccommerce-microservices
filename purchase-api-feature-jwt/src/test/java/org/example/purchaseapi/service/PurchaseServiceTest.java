package org.example.purchaseapi.service;

import org.example.purchaseapi.client.ProductClient;
import org.example.purchaseapi.client.UserClient;
import org.example.purchaseapi.dto.ProductResponse;
import org.example.purchaseapi.dto.ProductTypeResponse;
import org.example.purchaseapi.dto.PurchaseRequest;
import org.example.purchaseapi.dto.PurchaseResponse;
import org.example.purchaseapi.exception.NotEnoughStockException;
import org.example.purchaseapi.exception.PurchaseNotFoundException;
import org.example.purchaseapi.model.Purchase;
import org.example.purchaseapi.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private PurchaseService purchaseService;

    private Purchase purchase;
    private PurchaseRequest purchaseRequest;
    private PurchaseResponse purchaseResponse;

    private ProductResponse productResponse;

    private String token;
    Map<String, String> fakeClaims = new HashMap<>();

    @BeforeEach
    void setUp() {
        purchase = new Purchase(1L, 1L, 1L, 1);
        purchaseRequest = new PurchaseRequest(purchase.getProductId(), purchase.getQuantity());
        purchaseResponse = new PurchaseResponse(purchase.getId(), purchase.getUserId(), purchase.getProductId(), purchase.getQuantity());
        productResponse = new ProductResponse(1L, "Banana", 1, 2, new ProductTypeResponse(1, "Fruit"));

        token = "token";
        fakeClaims.put("userId", "1");
    }

    @Test
    void getAllPurchases_getPurchasesList_returnsAllPurchases() {
        // Given
        List<PurchaseResponse> expected = List.of(purchaseResponse);

        // When
        Mockito.when(purchaseRepository.findAll()).thenReturn(List.of(purchase));
        List<PurchaseResponse> actual = purchaseService.getAllPurchases();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void getAllPurchasesByUser_getPurchasesListByUser_returnsAllPurchasesByUser() {
        // Given
        List<PurchaseResponse> expected = List.of(purchaseResponse);

        // When
        Mockito.when(userClient.getClaims(token)).thenReturn(fakeClaims);
        Mockito.when(purchaseRepository.findAllByUserId(1L)).thenReturn(List.of(purchase));
        List<PurchaseResponse> actual = purchaseService.getAllPurchasesByUser(token);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void getAllPurchasesByUserIdAndProductType_productType_returnsAllPurchases() {
        // Given
        List<PurchaseResponse> expected = List.of(purchaseResponse);

        // When
        Mockito.when(userClient.getClaims(token)).thenReturn(fakeClaims);
        Mockito.when(purchaseRepository.findAllByUserId(1L)).thenReturn(List.of(purchase));
        Mockito.when(productClient.getProduct(purchase.getProductId(), token)).thenReturn(productResponse);
        List<PurchaseResponse> actual = purchaseService.getAllPurchasesByUserIdAndProductType(token, 1);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void getAllPurchasesByUserIdAndProductType_productType_filtersOutNonMatchingPurchases() {
        // Given
        List<PurchaseResponse> expected = List.of();

        ProductResponse nonMatchingProductResponse = new ProductResponse();
        nonMatchingProductResponse.setProductType(new ProductTypeResponse(2, "Fruit"));

        // When
        Mockito.when(userClient.getClaims(token)).thenReturn(fakeClaims);
        Mockito.when(purchaseRepository.findAllByUserId(1L)).thenReturn(List.of(purchase));
        Mockito.when(productClient.getProduct(purchase.getProductId(), token)).thenReturn(nonMatchingProductResponse);
        List<PurchaseResponse> actual = purchaseService.getAllPurchasesByUserIdAndProductType(token, 1);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void getPurchaseById_validId_returnPurchase() {
        // Given
        PurchaseResponse expected = purchaseResponse;

        // When
        Mockito.when(purchaseRepository.findById(purchase.getId())).thenReturn(Optional.ofNullable(purchase));
        PurchaseResponse actual = purchaseService.getPurchaseById(purchase.getId());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void getPurchaseById_invalidId_throwPurchaseNotFoundException() {
        // Given

        // When
        Mockito.when(purchaseRepository.findById(purchase.getId())).thenThrow(PurchaseNotFoundException.class);

        // Then
        assertThrows(PurchaseNotFoundException.class, () -> purchaseService.getPurchaseById(purchase.getId()));
    }

    @Test
    void createPurchase_userAndProductExists_returnsCreatedPurchase() {
        // Given
        PurchaseResponse expected = purchaseResponse;
        Map<String, String> fakeClaims = new HashMap<>();
        fakeClaims.put("userId", "1");

        // When
        Mockito.when(userClient.getClaims(token)).thenReturn(fakeClaims);
        Mockito.when(productClient.getProduct(purchase.getProductId(), token)).thenReturn(productResponse);
        Mockito.when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);
        PurchaseResponse actual = purchaseService.createPurchase(purchaseRequest, token);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void createPurchase_notEnoughStock_throwNotEnoughStockException() {
        // Given
        Map<String, String> fakeClaims = new HashMap<>();
        fakeClaims.put("userId", "1");

        // When
        Mockito.when(userClient.getClaims(token)).thenReturn(fakeClaims);
        Mockito.when(productClient.getProduct(purchase.getProductId(), token)).thenReturn(new ProductResponse(1L, "Banana", 1, 0, new ProductTypeResponse(1, "Fruit")));

        // Then
        assertThrows(NotEnoughStockException.class, () -> purchaseService.createPurchase(purchaseRequest, token));
    }
}