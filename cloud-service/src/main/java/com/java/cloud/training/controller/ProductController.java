package com.java.cloud.training.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import com.java.cloud.training.dto.product.ProductDto;
import com.java.cloud.training.dto.product.UpdateProductDto;
import com.java.cloud.training.service.ProductService;
import com.java.cloud.training.service.data.ProductSearchData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/products")
@Tag(name = "product", description = "the Product API")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Find product by code", description = "Returns a single product", tags = {"product"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @GetMapping("/{productCode}")
    public ResponseEntity<ProductDto> getProduct(
            @Parameter(description = "Code of product to be obtained. Cannot be empty", required = true, example = "note10") @PathVariable String productCode) {
        ProductDto dto = productService.getProduct(productCode);
        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @Operation(summary = "Delete product by code", tags = {"product"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @DeleteMapping("/{productCode}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Code of product to be removed. Cannot be empty", required = true, example = "galaxyS21") @PathVariable String productCode) {
        productService.deleteProduct(productCode);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "Create product", tags = {"product"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Product already exists"),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @PostMapping
    public ResponseEntity<Void> createProduct(@Parameter(description = "Product to be created", required = true, schema = @Schema(implementation = ProductDto.class))
                                          @Valid @RequestBody ProductDto productDto) {
        productService.createProduct(productDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @Operation(summary = "Get all products", tags = {"product"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)))),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @GetMapping(produces = {"application/json"})
    public ResponseEntity<List<ProductDto>> getProducts(@Parameter(description = "Name of product to search (like)", example = "redmi") @RequestParam(required = false) String name,
                                                        @Parameter(description = "The lower value of price range", example = "3999") @RequestParam(required = false) BigDecimal priceLow,
                                                        @Parameter(description = "The highest value of the price range", example = "26999") @RequestParam(required = false) BigDecimal priceHigh,
                                                        @Parameter(description = "Sort by. If passed malformed sort, default 'name,asc' sort applied", example ="name,asc") @RequestParam(defaultValue = "name,asc", required = false) String sort) {

        ProductSearchData productSearchData = ProductSearchData.builder()
                .name(name)
                .priceLow(priceLow)
                .priceHigh(priceHigh)
                .sort(sort)
                .build();
        List<ProductDto> result = productService.getProducts(productSearchData);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Update product by code", tags = {"product"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception"),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @PutMapping(value = "/{productCode}", produces = {"application/json"})
    public ResponseEntity<Void> updateProduct(@Parameter(description = "Code of product to be updated. Cannot be empty", required = true, example = "mi10T") @PathVariable String productCode
            ,@Parameter(description = "Product to be updated", required = true, schema = @Schema(implementation = UpdateProductDto.class))
                                                         @Valid @RequestBody UpdateProductDto updateProductDto) {
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(updateProductDto, productDto);
        productDto.setCode(productCode);
        productService.updateProduct(productDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
