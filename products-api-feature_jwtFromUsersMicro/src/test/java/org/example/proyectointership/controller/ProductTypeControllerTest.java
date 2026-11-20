package org.example.proyectointership.controller;

import org.example.proyectointership.client.AdminValidator;
import org.example.proyectointership.client.UserClient;
import org.example.proyectointership.exception.ResourceNotFoundException;
import org.example.proyectointership.exception.UserIsNotAdminException;
import org.example.proyectointership.model.ProductType;
import org.example.proyectointership.repository.ProductRepository;
import org.example.proyectointership.repository.ProductTypeRepository;
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
class ProductTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductTypeRepository productTypeRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private UserClient userClient;

    @MockitoBean
    private AdminValidator adminValidator;

    private ProductType productType;

    @BeforeEach
    void setUp() {
        productType = new ProductType(1, "Fruit");
        Mockito.doNothing().when(adminValidator).validateAdmin(anyString());
    }

    @Test
    void getProductType_validId_returnProductType() throws Exception {
        // Given
        Mockito.when(productTypeRepository.findById(productType.getId())).thenReturn(Optional.ofNullable(productType));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/productTypes/{id}", productType.getId()));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productType.getId()))
                .andExpect(jsonPath("$.name").value(productType.getName()));
    }

    @Test
    void getProductType_idNotInDB_returnResourceNotFound() throws Exception {
        // Given
        Mockito.when(productTypeRepository.findById(productType.getId())).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/productTypes/{id}", productType.getId()));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ProductType with id 1 not found"));
    }

    @Test
    void getAllProductTypes_request_returnProductTypesList() throws Exception {
        // Given
        Mockito.when(productTypeRepository.findAll()).thenReturn(List.of(productType));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/productTypes"));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(productType.getId()))
                .andExpect(jsonPath("$[0].name").value(productType.getName()));
    }

    @Test
    void createProductType_validProductAndIsAdmin_returnProductTypeResponse() throws Exception {
        // Given
        Mockito.when(productTypeRepository.save(any(ProductType.class))).thenReturn(productType);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/productTypes")
                .contentType("application/json")
                .content("{\"name\":\"" + productType.getName() + "\"}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(productType.getId()))
                .andExpect(jsonPath("$.name").value(productType.getName()));
    }

    @Test
    void createProductType_invalidProductAndIsAdmin_returnValidationFailedError() throws Exception {
        // Given

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/productTypes")
                .contentType("application/json")
                .content("{\"name\":\"\"}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid fields"));
    }

    @Test
    void createProductType_isUser_returnUserIsNotAdmin() throws Exception {
        // Given
        Mockito.doThrow(new UserIsNotAdminException(String.format("User with id %s is not admin", 1L)))
                .when(adminValidator).validateAdmin(anyString());

        Mockito.when(productTypeRepository.save(any(ProductType.class))).thenReturn(productType);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/productTypes")
                .contentType("application/json")
                .content("{\"name\":\"" + productType.getName() + "\"}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User with id 1 is not admin"));
    }

    @Test
    void updateProductType_validProductTypeAndIsAdmin_returnProductTypeResponse() throws Exception {
        // Given
        Mockito.when(productTypeRepository.findById(productType.getId())).thenReturn(Optional.ofNullable(productType));
        Mockito.when(productTypeRepository.save(productType)).thenReturn(productType);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/productTypes/{id}", productType.getId())
                .contentType("application/json")
                .content("{\"name\":\"" + productType.getName() + "\"}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productType.getId()))
                .andExpect(jsonPath("$.name").value(productType.getName()));
    }

    @Test
    void updateProductType_idNotInDBAndIsAdmin_returnResourceNotFound() throws Exception {
        // Given
        Mockito.when(productTypeRepository.save(any(ProductType.class))).thenThrow(new ResourceNotFoundException("ProductType with id 1 not found"));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/productTypes/{id}", productType.getId())
                .contentType("application/json")
                .content("{\"name\":\"" + productType.getName() + "\"}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ProductType with id 1 not found"));
    }

    @Test
    void updateProductType_invalidProductTypeAndIsAdmin_returnValidationFailed() throws Exception {
        // Given

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/productTypes/{id}", productType.getId())
                .contentType("application/json")
                .content("{\"name\":\"\"}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid fields"));
    }

    @Test
    void updateProductType_validProductAndIsUser_returnUserIsNotAdmin() throws Exception {
        // Given
        Mockito.doThrow(new UserIsNotAdminException(String.format("User with id %s is not admin", 1L)))
                .when(adminValidator).validateAdmin(anyString());


        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/productTypes/{id}", productType.getId())
                .contentType("application/json")
                .content("{\"name\":\"" + productType.getName() + "\"}")
                .header("Authorization", 1L));

        // Then
        perform.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User with id 1 is not admin"));
    }

    @Test
    void deleteProductType_validIdAndIsAdmin_returnProductTypeId() throws Exception {
        // Given
        Mockito.when(productTypeRepository.findById(productType.getId())).thenReturn(Optional.of(productType));
        Mockito.when(productRepository.existsByProductTypeId(productType.getId())).thenReturn(false);
        Mockito.doNothing().when(productTypeRepository).deleteById(productType.getId());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/api/productTypes/{id}", productType.getId()).header("Authorization", 1L));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(productType.getId()));
    }

    @Test
    void deleteProductType_validIdButHasProductsAndIsAdmin_returnDeletionNotAllowed() throws Exception {
        // Given
        Mockito.when(productTypeRepository.findById(productType.getId())).thenReturn(Optional.of(productType));
        Mockito.when(productRepository.existsByProductTypeId(productType.getId())).thenReturn(true);
        Mockito.doNothing().when(productTypeRepository).deleteById(productType.getId());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/api/productTypes/{id}", productType.getId()).header("Authorization", 1L));

        // Then
        perform.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot delete ProductType because it is associated with existing products"));
    }

    @Test
    void deleteProductType_idNotInDbAndIsAdmin_returnResourceNotFound() throws Exception {
        // Given
        Mockito.when(productTypeRepository.findById(productType.getId())).thenReturn(Optional.empty());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/api/productTypes/" + productType.getId()).header("Authorization", 1L));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ProductType with id 1 not found"));
    }

    @Test
    void deleteProductType_isUser_returnUserIsNotAdmin() throws Exception {
        // Given
        Mockito.doThrow(new UserIsNotAdminException(String.format("User with id %s is not admin", 1L)))
                .when(adminValidator).validateAdmin(anyString());
        Mockito.when(productTypeRepository.findById(productType.getId())).thenReturn(Optional.of(productType));
        Mockito.when(productRepository.existsByProductTypeId(productType.getId())).thenReturn(false);
        Mockito.doNothing().when(productTypeRepository).deleteById(productType.getId());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/api/productTypes/{id}", productType.getId()).header("Authorization", 1L));

        // Then
        perform.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User with id 1 is not admin"));
    }
}