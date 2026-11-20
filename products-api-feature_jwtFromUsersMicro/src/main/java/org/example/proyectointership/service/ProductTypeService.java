package org.example.proyectointership.service;

import org.example.proyectointership.client.AdminValidator;
import org.example.proyectointership.dto.ProductTypeDTO;
import org.example.proyectointership.exception.DeletionNotAllowedException;
import org.example.proyectointership.exception.ResourceNotFoundException;
import org.example.proyectointership.model.ProductType;
import org.example.proyectointership.repository.ProductRepository;
import org.example.proyectointership.repository.ProductTypeRepository;
import org.example.proyectointership.response.ProductTypeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductTypeService {

    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AdminValidator adminValidator;

    public ProductTypeResponse getProductTypeById(Integer id) throws ResourceNotFoundException {
        ProductType productType = productTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("ProductType with id %s not found", id)));
        return convertToResponse(productType);
    }

    public List<ProductTypeResponse> getAllProductTypes() {
        List<ProductType> products = productTypeRepository.findAll();
        List<ProductTypeResponse> productResponses = new ArrayList<>();
        for (ProductType productType : products) {
            productResponses.add(convertToResponse(productType));
        }
        return productResponses;
    }

    public ProductTypeResponse createProductType(ProductTypeDTO productTypeDTO, String token) {
        adminValidator.validateAdmin(token);
        return convertToResponse(productTypeRepository.save(convertToEntity(productTypeDTO)));
    }

    public ProductTypeResponse updateProductType(int id, ProductTypeDTO productTypeDTO, String token) {
        adminValidator.validateAdmin(token);
        ProductType productType = productTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("ProductType with id %s not found", id)));
        productType.setName(productTypeDTO.getName());
        return convertToResponse(productTypeRepository.save(productType));
    }

    public int deleteProductTypeById(int id, String token) throws DeletionNotAllowedException {
        adminValidator.validateAdmin(token);
        ProductType productType = productTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("ProductType with id %s not found", id)));

        boolean hasProducts = productRepository.existsByProductTypeId(id);
        if (hasProducts)
            throw new DeletionNotAllowedException("Cannot delete ProductType because it is associated with existing products");

        productTypeRepository.deleteById(id);
        return productType.getId();
    }

    private ProductTypeResponse convertToResponse(ProductType productType) {
        ProductTypeResponse productTypeResponse = new ProductTypeResponse();
        productTypeResponse.setId(productType.getId());
        productTypeResponse.setName(productType.getName());
        return productTypeResponse;
    }

    private ProductType convertToEntity(ProductTypeDTO productTypeDTO) {
        ProductType productType = new ProductType();
        productType.setName(productTypeDTO.getName());
        return productType;
    }
}
