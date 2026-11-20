package org.example.proyectointership.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.example.proyectointership.dto.ProductCreateDTO;
import org.example.proyectointership.dto.ProductUpdateDTO;
import org.example.proyectointership.exception.ErrorResponse;
import org.example.proyectointership.response.ProductResponse;
import org.example.proyectointership.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!"),
            @ApiResponse(responseCode = "404", description = "Not found", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable int id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!"),
            @ApiResponse(responseCode = "400", description = "Bad request - dto not valid", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "ProductType not found", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestHeader("Authorization") String token, @Valid @RequestBody ProductCreateDTO productCreateDTO) {
        return new ResponseEntity<>(productService.createProduct(productCreateDTO, token), HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!"),
            @ApiResponse(responseCode = "404", description = "ProductType not found", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request - dto not valid", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@RequestHeader("Authorization") String token, @PathVariable int id, @Valid @RequestBody ProductUpdateDTO productUpdateDTO) {
        return productService.updateProductById(id, productUpdateDTO, token);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!"),
            @ApiResponse(responseCode = "404", description = "Not found", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/{id}")
    public Integer deleteProduct(@RequestHeader("Authorization") String token, @PathVariable int id) {
        return productService.deleteProductById(id, token);
    }
}
