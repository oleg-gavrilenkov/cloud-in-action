package com.java.cloud.training.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ProductDto extends UpdateProductDto {

    @Schema(description = "Product code", example = "mi10T", required = true)
    @NotEmpty
    private String code;

}
