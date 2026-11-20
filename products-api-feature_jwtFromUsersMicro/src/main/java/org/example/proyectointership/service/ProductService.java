package org.example.proyectointership.service;

import org.example.proyectointership.client.AdminValidator;
import org.example.proyectointership.dto.ProductCreateDTO;
import org.example.proyectointership.dto.ProductUpdateDTO;
import org.example.proyectointership.exception.ResourceNotFoundException;
import org.example.proyectointership.model.Product;
import org.example.proyectointership.model.ProductType;
import org.example.proyectointership.repository.ProductRepository;
import org.example.proyectointership.repository.ProductTypeRepository;
import org.example.proyectointership.response.ProductResponse;
import org.example.proyectointership.response.ProductTypeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private AdminValidator adminValidator;

    public ProductResponse getProductById(Integer id) throws ResourceNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Product with id %s not found", id)));
        return convertToResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            productResponses.add(convertToResponse(product));
        }
        return productResponses;
    }

    public ProductResponse createProduct(ProductCreateDTO productCreateDTO, String token) {
        adminValidator.validateAdmin(token);
        return convertToResponse(productRepository.save(convertToEntity(productCreateDTO)));
    }

    public ProductResponse updateProductById(Integer id, ProductUpdateDTO productUpdateDTO, String token) throws ResourceNotFoundException {
        adminValidator.validateAdmin(token);
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Product with id %s not found", id)));

        if (productUpdateDTO.getName() != null) {
            product.setName(productUpdateDTO.getName());
        }
        if (productUpdateDTO.getPrice() != null) {
            product.setPrice(productUpdateDTO.getPrice());
        }
        if (productUpdateDTO.getStock() != null) {
            product.setStock(productUpdateDTO.getStock());
        }
        if (productUpdateDTO.getProductTypeId() != null) {
            ProductType productType = productTypeRepository.findById(productUpdateDTO.getProductTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("ProductType with id %s not found", productUpdateDTO.getProductTypeId())));
            product.setProductType(productType);
        }
        productRepository.save(product);

        return convertToResponse(product);

    }

    public Integer deleteProductById(int id, String token) throws ResourceNotFoundException {
        adminValidator.validateAdmin(token);
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Product with id %s not found", id)));
        productRepository.deleteById(id);
        return product.getId();
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setPrice(product.getPrice());
        productResponse.setStock(product.getStock());
        productResponse.setProductType(new ProductTypeResponse(
                product.getProductType().getId(),
                product.getProductType().getName()));
        return productResponse;
    }

    private Product convertToEntity(ProductCreateDTO productDTO) {
        ProductType productType = productTypeRepository.findById(productDTO.getProductTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("ProductType with ID %s not found", productDTO.getProductTypeId())));

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setProductType(productType);
        return product;
    }
}
