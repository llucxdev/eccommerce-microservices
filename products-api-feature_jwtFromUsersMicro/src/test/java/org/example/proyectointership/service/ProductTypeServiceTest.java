package org.example.proyectointership.service;

import org.example.proyectointership.client.AdminValidator;
import org.example.proyectointership.dto.ProductTypeDTO;
import org.example.proyectointership.exception.DeletionNotAllowedException;
import org.example.proyectointership.exception.ResourceNotFoundException;
import org.example.proyectointership.exception.UserIsNotAdminException;
import org.example.proyectointership.model.ProductType;
import org.example.proyectointership.repository.ProductRepository;
import org.example.proyectointership.repository.ProductTypeRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class ProductTypeServiceTest {

    @Mock
    private ProductTypeRepository productTypeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AdminValidator adminValidator;

    @InjectMocks
    private ProductTypeService productTypeService;

    private ProductType productType;
    private ProductTypeResponse productTypeResponse;
    private ProductTypeDTO productTypeDTO;
    private String token;

    @BeforeEach
    void setUp() {
        productType = new ProductType(1, "Fruit");
        productTypeResponse = new ProductTypeResponse(1, "Fruit");
        productTypeDTO = new ProductTypeDTO("Fruit");
        token = "token";
    }

    @Test
    void getProductTypeById_validId_returnsProductType() {
        // Given
        ProductTypeResponse expected = productTypeResponse;

        // When
        Mockito.when(productTypeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(productType));
        ProductTypeResponse actual = productTypeService.getProductTypeById(anyInt());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void getProductTypeById_invalidId_throwsResourceNotFoundException() {
        // Given

        // When
        Mockito.when(productTypeRepository.findById(anyInt())).thenThrow(ResourceNotFoundException.class);

        // Then
        assertThrows(ResourceNotFoundException.class, () -> productTypeService.getProductTypeById(anyInt()));
    }

    @Test
    void getAllProductTypes_request_returnProductTypeList() {
        // Given
        List<ProductTypeResponse> expected = List.of(productTypeResponse);

        // When
        Mockito.when(productTypeRepository.findAll()).thenReturn(List.of(productType));
        List<ProductTypeResponse> actual = productTypeService.getAllProductTypes();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void createProductType_isAdmin_returnsProductType() {
        // Given
        ProductTypeResponse expected = productTypeResponse;

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(token);
        Mockito.when(productTypeRepository.save(any(ProductType.class))).thenReturn(productType);
        ProductTypeResponse actual = productTypeService.createProductType(productTypeDTO, token);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void createProductType_isUser_returnsUserIsNotAdmin() {
        // Given

        // When
        Mockito.doThrow(UserIsNotAdminException.class).when(adminValidator).validateAdmin(token);

        // Then
        assertThrows(UserIsNotAdminException.class, () -> productTypeService.createProductType(productTypeDTO, token));
    }

    @Test
    void updateProductType_validIdAndIsAdmin_returnsProductType() {
        // Given
        ProductTypeResponse expected = productTypeResponse;

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(token);
        Mockito.when(productTypeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(productType));
        Mockito.when(productTypeRepository.save(any(ProductType.class))).thenReturn(productType);
        ProductTypeResponse actual = productTypeService.updateProductType(productType.getId(), productTypeDTO, token);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void updateProductType_invalidIdAndIsAdmin_throwsResourceNotFoundException() {
        // Given

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(token);
        Mockito.when(productTypeRepository.findById(anyInt())).thenThrow(ResourceNotFoundException.class);

        // Then
        assertThrows(ResourceNotFoundException.class, () -> productTypeService.updateProductType(productType.getId(), productTypeDTO, token));
    }

    @Test
    void updateProductType_validIdAndIsUser_throwsUserIsNotAdminException() {
        // Given

        // When
        Mockito.doThrow(UserIsNotAdminException.class).when(adminValidator).validateAdmin(token);

        // Then
        assertThrows(UserIsNotAdminException.class, () -> productTypeService.updateProductType(productType.getId(), productTypeDTO, token));
    }

    @Test
    void deleteProductType_validIdAndIsAdmin_returnId() {
        // Given
        int expected = productType.getId();

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(token);
        Mockito.when(productTypeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(productType));
        Mockito.when(productRepository.existsByProductTypeId(anyInt())).thenReturn(false);
        int actual = productTypeService.deleteProductTypeById(productType.getId(), token);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void deleteProductType_invalidIdAndIsAdmin_throwsResourceNotFoundException() {
        // Given

        // When
        Mockito.doNothing().when(adminValidator).validateAdmin(token);
        Mockito.when(productTypeRepository.findById(productType.getId())).thenThrow(ResourceNotFoundException.class);

        // Then
        assertThrows(ResourceNotFoundException.class, () -> productTypeService.deleteProductTypeById(productType.getId(), token));
    }

    @Test
    void deleteProductType_validIdAndIsUser_throwsUserIsNotAdmin() {
        // Given

        // When
        Mockito.doThrow(UserIsNotAdminException.class).when(adminValidator).validateAdmin(token);

        // Then
        assertThrows(UserIsNotAdminException.class, () -> productTypeService.deleteProductTypeById(productType.getId(), token));
    }

    @Test
    void deleteProductType_productsAssociated_throwsDeletionNotAllowedException() {
        // Given

        // When
        Mockito.when(productTypeRepository.findById(anyInt())).thenReturn(Optional.ofNullable(productType));
        Mockito.when(productRepository.existsByProductTypeId(anyInt())).thenReturn(true);

        // Then
        assertThrows(DeletionNotAllowedException.class, () -> productTypeService.deleteProductTypeById(productType.getId(), token));
    }
}
