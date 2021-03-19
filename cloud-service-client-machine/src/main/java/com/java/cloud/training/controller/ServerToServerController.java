package com.java.cloud.training.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

import java.net.http.HttpResponse;
import java.util.List;
import com.java.cloud.training.dto.CategoryDto;
import com.java.cloud.training.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/server-to-server")
public class ServerToServerController {

    @Value("${resourceServer.url}")
    private String resourceServerUrl;

    @Autowired
    private WebClient webClient;

    @Operation(summary = "Get all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductDto.class))))
    })
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getProducts() {
        List<ProductDto> productDtos = webClient.get().uri(resourceServerUrl + "/products")
                .retrieve()
                .bodyToFlux(ProductDto.class)
                .collectList()
                .block();
        return new ResponseEntity(productDtos, HttpStatus.OK);
    }

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
    })
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        List<CategoryDto> productDtos = webClient.get().uri(resourceServerUrl + "/categories")
                .retrieve()
                .bodyToFlux(CategoryDto.class)
                .collectList()
                .block();
        return new ResponseEntity(productDtos, HttpStatus.OK);
    }
    
    @Operation(summary = "Delete category by code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Not enough permissions to permorm operation")
    })
    @DeleteMapping("/categories/{categoryCode}")
    public ResponseEntity<Void> deleteCategory(@Parameter(description = "Code of category to be removed. Cannot be empty", required = true, example = "phones") @PathVariable String categoryCode) {
    	return webClient.method(HttpMethod.DELETE)
    		.uri(resourceServerUrl + "/categories/{categoryCode}", categoryCode)
    		.exchangeToMono(response -> {
    			return Mono.just(new ResponseEntity(response.statusCode()));
    		}).block();
    }
    
    
    @Operation(summary = "Delete product by code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Not enough permissions to permorm operation")
    })
    @DeleteMapping("/products/{productCode}")
    public ResponseEntity<Void> deleteProduct(@Parameter(description = "Code of product to be removed. Cannot be empty", required = true, example = "galaxyS21") @PathVariable String productCode) {
    	return webClient.method(HttpMethod.DELETE)
    		.uri(resourceServerUrl + "/products/{categoryCode}", productCode)
    		.exchangeToMono(response -> {
    			return Mono.just(new ResponseEntity(response.statusCode()));
    		}).block();
    }
}
