package org.example.purchaseapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.purchaseapi.client.ProductClient;
import org.example.purchaseapi.client.UserClient;
import org.example.purchaseapi.dto.ProductResponse;
import org.example.purchaseapi.dto.ProductTypeResponse;
import org.example.purchaseapi.dto.PurchaseRequest;
import org.example.purchaseapi.exception.ProductNotFoundException;
import org.example.purchaseapi.exception.UserNotFoundException;
import org.example.purchaseapi.model.Purchase;
import org.example.purchaseapi.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchaseRepository purchaseRepository;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private ProductClient productClient;

    private Purchase purchase;
    private PurchaseRequest purchaseRequest;
    private ProductResponse productResponse;

    private String token;
    Map<String, String> fakeClaims = new HashMap<>();

    @BeforeEach
    void setUp() {
        purchase = new Purchase(1L, 1L, 1L, 1);
        purchaseRequest = new PurchaseRequest(1L, 1);
        productResponse = new ProductResponse(1L, "Banana", 1, 1, new ProductTypeResponse(1, "Fruit"));
        token = "Bearer";
        fakeClaims.put("userId", "1");

    }

    @Test
    void getAllPurchases_request_returnPurchasesList() throws Exception {
        // Given
        Mockito.when(purchaseRepository.findAll()).thenReturn(List.of(purchase));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/purchases"));

        // Then
        perform.andExpect(status().isOk()).andExpect(jsonPath("$[0].purchaseId").value(purchase.getId()));
    }

    @Test
    void getAllPurchasesByUser_request_returnPurchasesListByUser() throws Exception {
        // Given
        Mockito.when(purchaseRepository.findAllByUserId(purchase.getUserId())).thenReturn(List.of(purchase));
        Mockito.when(userClient.getClaims(token)).thenReturn(fakeClaims);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/purchases/user").header("Authorization", token));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].purchaseId").value(purchase.getId()))
                .andExpect(jsonPath("$[0].userId").value(purchase.getUserId()));
    }

    @Test
    void getAllPurchasesByUserAndProductType_request_returnPurchasesListByUserAndProductType() throws Exception {
        // Given
        Mockito.when(purchaseRepository.findAllByUserId(purchase.getUserId())).thenReturn(List.of(purchase));
        Mockito.when(productClient.getProduct(purchase.getProductId(), token)).thenReturn(productResponse);
        Mockito.when(userClient.getClaims(token)).thenReturn(fakeClaims);


        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/purchases/user/product-type/{productType}", purchase.getUserId(), 1).header("Authorization", token));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].purchaseId").value(purchase.getId()))
                .andExpect(jsonPath("$[0].userId").value(purchase.getUserId()));
    }

    @Test
    void getPurchaseById_validId_returnPurchase() throws Exception {
        // Given
        Mockito.when(purchaseRepository.findById(purchase.getId())).thenReturn(Optional.ofNullable(purchase));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/purchases/{id}", purchase.getId()));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.purchaseId").value(purchase.getId()));
    }

    @Test
    void getPurchaseById_invalidId_throwPurchaseNotFoundException() throws Exception {
        // Given
        Mockito.when(purchaseRepository.findById(purchase.getId())).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/purchases/{id}", purchase.getId()));

        // Then
        perform.andExpect(status().isNotFound());
    }

    @Test
    void createPurchase_validInput_returnPurchase() throws Exception {
        // Given
        Mockito.when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);
        ObjectMapper objectMapper = new ObjectMapper();
        String purchaseJson = objectMapper.writeValueAsString(purchaseRequest);
        Mockito.when(productClient.getProduct(purchase.getProductId(), token)).thenReturn(productResponse);
        Mockito.when(userClient.getClaims(token)).thenReturn(fakeClaims);


        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/purchases")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(purchaseJson));

        // Then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.purchaseId").value(purchase.getId()))
                .andExpect(jsonPath("$.userId").value(purchase.getUserId()))
                .andExpect(jsonPath("$.productId").value(purchase.getProductId()))
                .andExpect(jsonPath("$.quantity").value(purchase.getQuantity()));
    }

    @Test
    void createPurchase_invalidPurchaseDto_throwValidationException() throws Exception {
        // Given
        Mockito.when(userClient.getClaims(token)).thenReturn(fakeClaims);


        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/purchases")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").isNotEmpty());
    }

    @Test
    void createPurchase_userNotInDatabase_throwUserNotFoundException() throws Exception {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        String purchaseJson = objectMapper.writeValueAsString(purchase);
        Mockito.when(userClient.getClaims(token)).thenThrow(new UserNotFoundException("User not found"));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/purchases")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(purchaseJson));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void createPurchase_productNotInDatabase_throwUserNotFoundException() throws Exception {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        String purchaseJson = objectMapper.writeValueAsString(purchase);
        Mockito.when(productClient.getProduct(purchase.getProductId(), token)).thenThrow(new ProductNotFoundException("Product not found"));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/purchases")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(purchaseJson));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void createPurchase_notEnoughStock_throwUserNotFoundException() throws Exception {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        String purchaseJson = objectMapper.writeValueAsString(purchase);
        ProductResponse productWithoutStock = productResponse;
        productWithoutStock.setStock(0);
        Mockito.when(productClient.getProduct(purchase.getProductId(), token)).thenReturn(productWithoutStock);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/purchases")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(purchaseJson));

        // Then
        perform.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Not enough stock"));
    }
}