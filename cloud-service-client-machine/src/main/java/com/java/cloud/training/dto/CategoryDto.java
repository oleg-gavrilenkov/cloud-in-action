package com.java.cloud.training.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CategoryDto implements Serializable {

    @Schema(description = "Category code", example = "phones", required = true)
    @NotBlank
    private String code;
    @Schema(description = "Category Name", example = "Mobile Phones", required = true)
    @NotBlank
    private String name;
    @Schema(description = "List of category's products codes. If product with specified code doesn't exist, code is ignored", example = "[\"note10\"]")
    private List<String> products;
}
