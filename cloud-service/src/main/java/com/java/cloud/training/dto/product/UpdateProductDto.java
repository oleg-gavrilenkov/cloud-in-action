package com.java.cloud.training.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UpdateProductDto implements Serializable {

    @Schema(description = "Name of the product", example = "Mi 10T", required = true)
    @NotEmpty
    private String name;
    @Schema(description = "Price of the product", example = "15999", required = true)
    @NotNull
    private BigDecimal price;
    @Schema(description = "List of product's categories codes. If category with specified code doesn't exist, code is ignored", example = "[\"phones\",\"xiaomi\"]")
    private List<String> categories;

}
