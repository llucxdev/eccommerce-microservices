package org.example.proyectointership.service;

import org.example.proyectointership.client.AdminValidator;
import org.example.proyectointership.dto.ProductCreateDTO;
import org.example.proyectointership.dto.ProductUpdateDTO;
import org.example.proyectointership.exception.ResourceNotFoundException;
import org.example.proyectointership.exception.UserIsNotAdminException;
import org.example.proyectointership.model.Product;
import org.example.proyectointership.model.ProductType;
import org.example.proyectointership.repository.ProductRepository;
import org.example.proyectointership.repository.ProductTypeRepository;
import org.example.proyectointership.response.ProductResponse;
import org.example.proyectointership.response.ProductTypeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductTypeRepository productTypeRepository;

    @Mock
    private AdminValidator adminValidator;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductType productType;
    private ProductResponse productResponse;
    private ProductTypeResponse productTypeResponse;
    private String userId;

    @BeforeEach
    void setUp() {
        productType = new ProductType(1, "Fruit");
        product = new Product(1, "Kiwi", 2, 4, productType);
        productTypeResponse = new ProductTypeResponse(1, "Fruit");
        productResponse = new ProductResponse(1, "Kiwi", 2, 4, productTypeResponse);
        userId = "token";
    }

    @Test
    void getProductById_validId_returnProductResponse() {
        // Given
        ProductResponse expected = productResponse;

        // When
        Mockito.when(productRepository.findById(anyInt())).thenReturn(Optional.ofNullable(product));
        ProductResponse actual = productService.getProductById(anyInt());

        // Then
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getStock(), actual.getStock());
        assertEquals(expected.getProductType().getId(), actual.getProductType().getId());
        assertEquals(expected.getProductType().getName(), actual.getProductType().getName());
    }

    @Test
    void getProductById_invalidId_throwsResourceNotFoundException() {
        // Given

        // When
        Mockito.when(productRepository.findById(anyInt())).thenThrow(ResourceNotFoundException.class);

        // Then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(anyInt()));
    }

    @Test
    void getAllProducts_request_returnProductResponseList() {
        // Given
        List<ProductResponse> expected = List.of(productResponse);

        // Then
        Mockito.when(productRepository.findAll()).thenReturn(List.of(product));
        List<ProductResponse> actual = productService.getAllProducts();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void createProduct_isAdmin_returnProductResponse() {
        // Given
        ProductResponse expected = productResponse;

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(userId);
        Mockito.when(productTypeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(productType));
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductResponse actual = productService.createProduct(new ProductCreateDTO("Kiwi", 2, 3, 1), userId);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void createProduct_isUser_returnUserIsNotAdmin() {
        // Given

        // When
        Mockito.doThrow(UserIsNotAdminException.class).when(adminValidator).validateAdmin(userId);

        // Then
        assertThrows(UserIsNotAdminException.class, () -> productService.createProduct(new ProductCreateDTO("Kiwi", 2, 3, 1), userId));
    }

    @Test
    void updateProductById_validProductDTOAndIsAdmin_returnProductByIdResponse() {
        // Given
        ProductResponse expected = productResponse;

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(userId);
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.ofNullable(product));
        Mockito.when(productTypeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(productType));
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductResponse actual = productService.updateProductById(product.getId(), new ProductUpdateDTO("name", 1d, 1, 1), userId);

        // Then
        assertNotEquals(expected, actual);
    }

    @Test
    void updateProductById_nullDTOAndIsAdmin_returnsProductByIdResponse() {
        // Given
        ProductResponse expected = productResponse;

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(userId);
        Mockito.when(productRepository.findById(anyInt())).thenReturn(Optional.ofNullable(product));
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductResponse actual = productService.updateProductById(anyInt(), new ProductUpdateDTO(null, null, null, null), userId);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void updateProductById_invalidProductByIdAndIsAdmin_throwsResourceNotFoundException() {
        // Given

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(userId);
        Mockito.when(productRepository.findById(anyInt())).thenThrow(ResourceNotFoundException.class);

        // Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProductById(anyInt(), new ProductUpdateDTO("name", 1d, 1, 1), userId));
    }

    @Test
    void updateProductById_invalidProductByIdTypeIdAndIsAdmin_throwsResourceNotFoundException() {
        // Given

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(userId);
        Mockito.when(productRepository.findById(anyInt())).thenReturn(Optional.ofNullable(product));
        Mockito.when(productTypeRepository.findById(anyInt())).thenThrow(ResourceNotFoundException.class);

        // Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProductById(anyInt(), new ProductUpdateDTO("name", 1d, 1, 1), userId));
    }

    @Test
    void updateProductById_validProductByIdAndIsUser_returnUserIsNotAdmin() {
        // Given

        // When
        Mockito.doThrow(UserIsNotAdminException.class).when(adminValidator).validateAdmin(userId);

        // Then
        assertThrows(UserIsNotAdminException.class, () -> productService.updateProductById(anyInt(), new ProductUpdateDTO("name", 1d, 1, 1), userId));
    }

    @Test
    void deleteProductById_validIdAndIsAdmin_returnId() {
        // Given
        int expected = product.getId();

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(userId);
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.ofNullable(product));
        int actual = productService.deleteProductById(product.getId(), userId);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void deleteProductById_invalidIdAndIsAdmin_throwsResourceNotFoundException() {
        // Given

        // When
        Mockito.when(productRepository.findById(product.getId())).thenThrow(ResourceNotFoundException.class);

        // Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProductById(product.getId(), userId));
    }

    @Test
    void deleteProductById_validIdAndIsUser_returnUserIsNotAdmin() {
        // Given

        // When
        Mockito.doThrow(UserIsNotAdminException.class).when(adminValidator).validateAdmin(userId);

        // Then
        assertThrows(UserIsNotAdminException.class, () -> productService.deleteProductById(anyInt(), userId));
    }
}