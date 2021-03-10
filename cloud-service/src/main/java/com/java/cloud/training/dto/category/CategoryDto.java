package com.java.cloud.training.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CategoryDto extends UpdateCategoryDto {

    @Schema(description = "Category code", example = "phones", required = true)
    @NotBlank
    private String code;
}
