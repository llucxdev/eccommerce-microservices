package org.example.purchaseapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.purchaseapi.dto.PurchaseRequest;
import org.example.purchaseapi.dto.PurchaseResponse;
import org.example.purchaseapi.exception.ErrorResponse;
import org.example.purchaseapi.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@Tag(name = "Purchases", description = "API for managing purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @Operation(summary = "Get all purchases", description = "Returns a list of all registered purchases")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase list retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> getAllPurchases() {
        return new ResponseEntity<>(purchaseService.getAllPurchases(), HttpStatus.OK);
    }

    @Operation(summary = "Get purchases by user", description = "Returns all purchases made by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase list for the user retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseResponse.class)))
    })
    @GetMapping("/user")
    public ResponseEntity<List<PurchaseResponse>> getAllPurchasesByUser(@RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(purchaseService.getAllPurchasesByUser(token), HttpStatus.OK);
    }

    @Operation(summary = "Get purchases by user and product type", description = "Returns purchases of a specific user filtered by product type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filtered purchase list retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseResponse.class)))
    })
    @GetMapping("/user/product-type/{productType}")
    public ResponseEntity<List<PurchaseResponse>> getAllPurchasesByUserAndProductType(@RequestHeader("Authorization") String token, @PathVariable int productType) {
        return new ResponseEntity<>(purchaseService.getAllPurchasesByUserIdAndProductType(token, productType), HttpStatus.OK);
    }

    @Operation(summary = "Get purchase by ID", description = "Returns the details of a specific purchase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Purchase details retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Purchase not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getPurchaseById(@PathVariable Long id) {
        return new ResponseEntity<>(purchaseService.getPurchaseById(id), HttpStatus.OK);
    }

    @Operation(summary = "Create a new purchase", description = "Allows registering a new purchase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Purchase created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PurchaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product or User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Not enough stock",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<PurchaseResponse> createPurchase(@RequestHeader("Authorization") String token, @Valid @RequestBody PurchaseRequest purchaseRequest) {
        return new ResponseEntity<>(purchaseService.createPurchase(purchaseRequest, token), HttpStatus.CREATED);
    }
}