package org.example.proyectointership.controller;

import org.example.proyectointership.client.AdminValidator;
import org.example.proyectointership.client.UserClient;
import org.example.proyectointership.exception.UserIsNotAdminException;
import org.example.proyectointership.model.Product;
import org.example.proyectointership.model.ProductType;
import org.example.proyectointership.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private AdminValidator adminValidator;

    private ProductType productType;
    private Product product;

    @BeforeEach
    void setUp() {
        productType = new ProductType(1, "Fruit");
        product = new Product(1, "Kiwi", 1, 1, productType);
        Mockito.doNothing().when(adminValidator).validateAdmin(anyString());
    }

    @Test
    void getProduct_validId_returnProductResponse() throws Exception {
        // Given
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.ofNullable(product));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/" + product.getId()));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.stock").value(product.getStock()))
                .andExpect(jsonPath("$.productType.id").value(productType.getId()))
                .andExpect(jsonPath("$.productType.name").value(productType.getName()));
    }

    @Test
    void getProduct_idNotInDB_returnResourceNotFound() throws Exception {
        // Given
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/" + product.getId()));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with id 1 not found"));
    }

    @Test
    void getAllProducts_request_returnProductsListResponse() throws Exception {
        // Given
        Mockito.when(productRepository.findAll()).thenReturn(List.of(product));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/products"));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(product.getId()))
                .andExpect(jsonPath("$[0].name").value(product.getName()))
                .andExpect(jsonPath("$[0].price").value(product.getPrice()))
                .andExpect(jsonPath("$[0].stock").value(product.getStock()))
                .andExpect(jsonPath("$[0].productType.id").value(productType.getId()))
                .andExpect(jsonPath("$[0].productType.name").value(productType.getName()));

    }

    @Test
    void createProduct_validProductAndIsAdmin_returnProductResponse() throws Exception {
        // Given
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(product);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                .contentType("application/json")
                .content("{\"name\":\"Kiwi\",\"price\":1.0, \"stock\":1, \"productTypeId\":1}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(product.getId()));
    }

    @Test
    void createProduct_invalidProductAndIsAdmin_returnValidationFailedException() throws Exception {
        // Given

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/products").content("application/json")
                .contentType("application/json")
                .content("{\"name\":\"\",\"price\":0, \"stock\":0, \"productTypeId\":0}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid fields"))
                .andExpect(jsonPath("$.validationErrors").isNotEmpty());
    }

    @Test
    void createProduct_isUser_returnUserIsNotAdmin() throws Exception {
        // Given
        Mockito.doThrow(new UserIsNotAdminException(String.format("User with id %s is not admin", 1L)))
                .when(adminValidator).validateAdmin(anyString());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/products").content("application/json")
                .contentType("application/json")
                .content("{\"name\":\"Kiwi\",\"price\":1.0, \"stock\":1, \"productTypeId\":1}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(String.format("User with id %s is not admin", 1L)));
    }

    @Test
    void updateProduct_validProductAndIsAdmin_returnProductResponse() throws Exception {
        // Given
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.ofNullable(product));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/products/" + product.getId())
                .contentType("application/json")
                .content("{\"name\":\"Kiwi\",\"price\":1.0, \"stock\":1, \"productTypeId\":1}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.stock").value(product.getStock()))
                .andExpect(jsonPath("$.productType.id").value(productType.getId()))
                .andExpect(jsonPath("$.productType.name").value(productType.getName()));
    }

    @Test
    void updateProduct_idNotInDBAndIsAdmin_returnResourceNotFound() throws Exception {
        // Given
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/products/" + product.getId())
                .contentType("application/json")
                .content("{\"name\":\"Kiwi\",\"price\":1.0, \"stock\":1, \"productTypeId\":1}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with id 1 not found"));
    }

    @Test
    void updateProduct_invalidProductAndIsAdmin_returnValidationFailed() throws Exception {
        // Given

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/products/" + product.getId())
                .contentType("application/json")
                .content("{\"price\":0}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid fields"))
                .andExpect(jsonPath("$.validationErrors").isNotEmpty());
    }

    @Test
    void updateProduct_isUser_returnUserIsNotAdmin() throws Exception {
        // Given
        Mockito.doThrow(new UserIsNotAdminException(String.format("User with id %s is not admin", 1L)))
                .when(adminValidator).validateAdmin(anyString());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/products/" + product.getId())
                .contentType("application/json")
                .content("{\"name\":\"Kiwi\",\"price\":1.0, \"stock\":1, \"productTypeId\":1}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isForbidden()).andExpect(jsonPath("$.message").value("User with id 1 is not admin"));
    }

    @Test
    void deleteProduct_validIdAndIsAdmin_returnProductId() throws Exception {
        // Given
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Mockito.doNothing().when(productRepository).deleteById(product.getId());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/" + product.getId()).header("Authorization", 1L));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(product.getId()));
    }

    @Test
    void deleteProduct_idNotInDBAndIsAdmin_returnResourceNotFound() throws Exception {
        // Given
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/" + product.getId()).header("Authorization", 1L));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product with id 1 not found"));
    }

    @Test
    void deleteProduct_isUser_returnUserIsNotAdmin() throws Exception {
        // Given
        Mockito.doThrow(new UserIsNotAdminException(String.format("User with id %s is not admin", 1L)))
                .when(adminValidator).validateAdmin(anyString());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/" + product.getId()).header("Authorization", 1L));

        // Then
        perform.andExpect(status().isForbidden()).andExpect(jsonPath("$.message").value("User with id 1 is not admin"));
    }
}