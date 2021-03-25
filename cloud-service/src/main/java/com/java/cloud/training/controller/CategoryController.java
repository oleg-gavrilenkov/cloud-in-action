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
import com.java.cloud.training.dto.category.CategoryDto;
import com.java.cloud.training.dto.category.UpdateCategoryDto;
import com.java.cloud.training.dto.product.ProductDto;
import com.java.cloud.training.payroll.CategoryDtoAssembler;
import com.java.cloud.training.service.CategoryService;
import com.java.cloud.training.service.ProductService;
import com.java.cloud.training.service.data.CategorySearchData;
import com.java.cloud.training.service.data.ProductSearchData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@Tag(name = "category", description = "the Category API")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryDtoAssembler categoryDtoAssembler;

    @Operation(summary = "Find category by code", description = "Returns a single category", tags = {"category"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found",
                         content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @GetMapping("/{categoryCode}")
    public ResponseEntity<CategoryDto> getCategory(
            @Parameter(description = "Code of category to be obtained. Cannot be empty", required = true, example = "phones") @PathVariable String categoryCode) {
        CategoryDto dto = categoryService.getCategory(categoryCode);
        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @Operation(summary = "Find products related to category", description = "List of products", tags = {"category"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)))),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @GetMapping(value = "/{categoryCode}/products", produces = {"application/json"})
    public ResponseEntity<List<ProductDto>> getCategoryProducts(@Parameter(description = "Code of category. Cannot be empty", required = true, example = "phones") @PathVariable String categoryCode,
                                                                @Parameter(description = "Name of product to search (like)", example = "redmi") @RequestParam(required = false) String name,
                                                                @Parameter(description = "The lower value of price range", example = "3999") @RequestParam(required = false)
                                                                             BigDecimal priceLow,
                                                                @Parameter(description = "The highest value of the price range", example = "26999") @RequestParam(required = false) BigDecimal priceHigh,
                                                                @Parameter(description = "Sort by. If passed malformed sort, default 'name,asc' sort applied", example ="name,asc") @RequestParam(defaultValue = "name,asc", required = false) String sort) {
        categoryService.getCategory(categoryCode);
        ProductSearchData productSearchData = ProductSearchData.builder()
                .categoryCode(categoryCode)
                .name(name)
                .priceLow(priceLow)
                .priceHigh(priceHigh)
                .sort(sort)
                .build();
        List<ProductDto> products = productService.getProducts(productSearchData);
        return new ResponseEntity(products, HttpStatus.OK);
    }

    @Operation(summary = "Get all categories", tags = {"category"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class)))),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @GetMapping(produces = {"application/json"})
    public ResponseEntity<List<CategoryDto>> getCategories(@Parameter(description = "Name of category to search (like)", example = "Watches") @RequestParam(required = false) String name,
                                                           @Parameter(description = "Sort by. If passed malformed sort, default 'products,asc' applied", example ="products,asc") @RequestParam(defaultValue = "products,asc", required = false) String sort) {
        CategorySearchData categorySearchData = CategorySearchData.builder()
                .name(name)
                .sort(sort)
                .build();
        List<CategoryDto> result = categoryService.getCategories(categorySearchData);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @Operation(summary = "Create category", tags = {"category"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Category already exists"),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EntityModel<CategoryDto> createCategory(@Parameter(description = "Category to be created", required = true, schema = @Schema(implementation = CategoryDto.class))
                                              @Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategoryDto = categoryService.createCategory(categoryDto);
        return categoryDtoAssembler.toModel(createdCategoryDto);
    }

    @Operation(summary = "Delete category by code", tags = {"category"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @DeleteMapping("/{categoryCode}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Code of category to be removed. Cannot be empty", required = true, example = "phones") @PathVariable String categoryCode) {
        categoryService.deleteCategory(categoryCode);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "Update category by code", tags = {"category"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception"),
            @ApiResponse(responseCode = "401", description = "Not authorized request"),
            @ApiResponse(responseCode = "403", description = "Not enough permissions to perform operation")
    })
    @PutMapping(value = "/{categoryCode}", produces = {"application/json"})
    public ResponseEntity<Void> updateCategory(@Parameter(description = "Code of category to be updated. Cannot be empty", required = true, example = "phones") @PathVariable String categoryCode
            ,@Parameter(description = "Product to be updated", required = true, schema = @Schema(implementation = UpdateCategoryDto.class))
                                              @Valid @RequestBody UpdateCategoryDto updateCategoryDto) {
        CategoryDto categoryDto = new CategoryDto();
        BeanUtils.copyProperties(updateCategoryDto, categoryDto);
        categoryDto.setCode(categoryCode);
        categoryService.updateCategory(categoryDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
