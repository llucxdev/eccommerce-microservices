package org.example.proyectointership.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.example.proyectointership.dto.ProductTypeDTO;
import org.example.proyectointership.exception.ErrorResponse;
import org.example.proyectointership.response.ProductTypeResponse;
import org.example.proyectointership.service.ProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productTypes")
public class ProductTypeController {

    @Autowired
    private ProductTypeService productTypeService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!"),
            @ApiResponse(responseCode = "404", description = "ProductType not found", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{id}")
    public ProductTypeResponse getProductType(@PathVariable int id) {
        return productTypeService.getProductTypeById(id);
    }

    @GetMapping
    public List<ProductTypeResponse> getAllProductTypes() {
        return productTypeService.getAllProductTypes();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!"),
            @ApiResponse(responseCode = "400", description = "Bad request - dto not valid", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping
    public ResponseEntity<ProductTypeResponse> createProductType(@RequestHeader("Authorization") String token, @Valid @RequestBody ProductTypeDTO productTypeDTO) {
        return new ResponseEntity<>(productTypeService.createProductType(productTypeDTO, token), HttpStatus.CREATED);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!"),
            @ApiResponse(responseCode = "400", description = "Bad request - dto not valid", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PutMapping("{id}")
    public ProductTypeResponse updateProductType(@RequestHeader("Authorization") String token, @PathVariable int id, @Valid @RequestBody ProductTypeDTO productTypeDTO) {
        return productTypeService.updateProductType(id, productTypeDTO, token);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success!"),
            @ApiResponse(responseCode = "404", description = "ProductType not found", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/{id}")
    public Integer deleteProductType(@RequestHeader("Authorization") String token, @PathVariable int id) {
        return productTypeService.deleteProductTypeById(id, token);
    }

}
